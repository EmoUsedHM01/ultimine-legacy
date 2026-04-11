package com.ftbultimine.handler;

import java.util.List;

import com.ftbultimine.Config;
import com.ftbultimine.PlayerDataManager;
import com.ftbultimine.PlayerDataManager.PlayerData;
import com.ftbultimine.shape.BlockPosHelper.MutableBlockPos;
import com.ftbultimine.shape.Shape;
import com.ftbultimine.shape.ShapeRegistry;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.BlockEvent;

public class UltimineHandler {

    private static boolean isProcessing = false;

    public static void init() {
        UltimineHandler handler = new UltimineHandler();
        MinecraftForge.EVENT_BUS.register(handler);
        FMLCommonHandler.instance().bus().register(handler);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (isProcessing) return;

        EntityPlayer player = event.getPlayer();
        if (player == null || player.worldObj.isRemote) return;
        if (!(player instanceof EntityPlayerMP)) return;

        PlayerData data = PlayerDataManager.get(player);
        if (!data.isUltimineActive()) return;

        // Cooldown check
        long currentTime = player.worldObj.getTotalWorldTime();
        if (data.isOnCooldown(currentTime)) return;

        // Tool check
        ItemStack heldItem = player.getCurrentEquippedItem();
        if (Config.requireTool && (heldItem == null || !heldItem.getItem().isDamageable())) {
            return;
        }

        World world = event.world;
        int originX = event.x;
        int originY = event.y;
        int originZ = event.z;
        Block originBlock = event.block;
        int originMeta = event.blockMetadata;

        // Get the face from the player's look direction
        ForgeDirection face = getBreakFace(player);

        // Get blocks to break
        Shape shape = ShapeRegistry.getShape(data.getCurrentShape());
        List<MutableBlockPos> blocks = shape.getBlocks(world, player, originX, originY, originZ,
            face, Config.maxBlocks);

        // Remove the origin block (it's already being broken by the event)
        blocks.remove(new MutableBlockPos(originX, originY, originZ));

        if (blocks.isEmpty()) return;

        // Set cooldown
        data.setLastUltimineTime(currentTime);

        // Break all the extra blocks
        isProcessing = true;
        try {
            int broken = 0;
            for (MutableBlockPos pos : blocks) {
                if (broken >= Config.maxBlocks - 1) break;

                Block block = world.getBlock(pos.x, pos.y, pos.z);
                int meta = world.getBlockMetadata(pos.x, pos.y, pos.z);

                if (block.isAir(world, pos.x, pos.y, pos.z)) continue;

                // Check tool durability - stop before the tool would break
                if (Config.preventToolBreak && heldItem != null && heldItem.getItem().isDamageable()) {
                    if (heldItem.getItemDamage() >= heldItem.getMaxDamage()) {
                        break;
                    }
                }

                // Try to break the block using the player's method (respects protection mods)
                if (!breakBlockAsPlayer((EntityPlayerMP) player, world, pos.x, pos.y, pos.z)) {
                    if (Config.cancelOnBlockBreakFail) break;
                    continue;
                }

                broken++;

                // Apply exhaustion
                if (Config.exhaustionPerBlock > 0) {
                    player.addExhaustion((float) (Config.exhaustionPerBlock / 100.0));
                }

                // Apply experience cost
                if (Config.experiencePerBlock > 0 && player.experienceLevel > 0) {
                    // Deduct XP (simplified)
                    float cost = (float) Config.experiencePerBlock;
                    if (player.experience < cost / player.xpBarCap()) {
                        // Not enough XP - stop
                        break;
                    }
                }

                // Damage tool
                if (heldItem != null && heldItem.getItem().isDamageable()) {
                    heldItem.damageItem(1, player);
                    if (heldItem.stackSize <= 0) {
                        player.destroyCurrentEquippedItem();
                        break;
                    }
                }
            }
        } finally {
            isProcessing = false;
        }
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        PlayerDataManager.remove(event.player);
    }

    /**
     * Break a block as if the player broke it, firing events and dropping items.
     */
    private boolean breakBlockAsPlayer(EntityPlayerMP player, World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);

        // Check if the player can actually harvest this block with their current tool
        if (!net.minecraftforge.common.ForgeHooks.canHarvestBlock(block, player, meta)) {
            return false;
        }

        // Fire break event
        BlockEvent.BreakEvent breakEvent = new BlockEvent.BreakEvent(
            x, y, z, world, block, meta, player);
        MinecraftForge.EVENT_BUS.post(breakEvent);

        if (breakEvent.isCanceled()) {
            return false;
        }

        // Drop items
        block.harvestBlock(world, player, x, y, z, meta);

        // Give XP
        int xp = breakEvent.getExpToDrop();
        if (xp > 0) {
            block.dropXpOnBlockBreak(world, x, y, z, xp);
        }

        // Remove the block
        world.setBlockToAir(x, y, z);

        return true;
    }

    /**
     * Server-side raytrace to determine which face the player is looking at.
     * This matches the client-side MovingObjectPosition.sideHit used by the outline renderer.
     */
    private ForgeDirection getBreakFace(EntityPlayer player) {
        double reach = player instanceof EntityPlayerMP
            ? ((EntityPlayerMP) player).theItemInWorldManager.getBlockReachDistance()
            : 5.0;
        Vec3 eyePos = Vec3.createVectorHelper(
            player.posX,
            player.posY + (double) player.getEyeHeight(),
            player.posZ);
        Vec3 lookVec = player.getLookVec();
        Vec3 endPos = Vec3.createVectorHelper(
            eyePos.xCoord + lookVec.xCoord * reach,
            eyePos.yCoord + lookVec.yCoord * reach,
            eyePos.zCoord + lookVec.zCoord * reach);

        MovingObjectPosition mop = player.worldObj.rayTraceBlocks(eyePos, endPos);
        if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            return ForgeDirection.getOrientation(mop.sideHit);
        }

        // Fallback if raytrace fails
        return ForgeDirection.getOrientation(
            com.ftbultimine.shape.ShapedShapes.getPlayerFacingHorizontal(player).ordinal());
    }
}
