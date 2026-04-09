package com.ftbultimine.handler;

import java.util.List;

import com.ftbultimine.Config;
import com.ftbultimine.PlayerDataManager;
import com.ftbultimine.PlayerDataManager.PlayerData;
import com.ftbultimine.shape.BlockPosHelper.MutableBlockPos;
import com.ftbultimine.shape.Shape;
import com.ftbultimine.shape.ShapeRegistry;
import com.ftbultimine.shape.ShapedShapes;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGrass;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class RightClickHandler {

    private static boolean isProcessing = false;

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new RightClickHandler());
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (isProcessing) return;
        if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) return;
        if (event.entityPlayer == null || event.entityPlayer.worldObj.isRemote) return;

        EntityPlayer player = event.entityPlayer;
        PlayerData data = PlayerDataManager.get(player);
        if (!data.isUltimineActive()) return;

        ItemStack heldItem = player.getCurrentEquippedItem();
        if (heldItem == null) return;

        World world = event.world;
        int x = event.x;
        int y = event.y;
        int z = event.z;
        ForgeDirection face = ForgeDirection.getOrientation(event.face);

        // Determine which right-click action to perform
        if (Config.rightClickHoe && heldItem.getItem() instanceof ItemHoe) {
            handleHoeTilling(world, player, x, y, z, face, data, heldItem);
        } else if (Config.rightClickHarvesting && isMatureCrop(world, x, y, z)) {
            handleCropHarvesting(world, player, x, y, z, face, data);
        }
    }

    private void handleHoeTilling(World world, EntityPlayer player, int x, int y, int z,
                                   ForgeDirection face, PlayerData data, ItemStack heldItem) {
        Shape shape = ShapeRegistry.getShape(data.getCurrentShape());
        List<MutableBlockPos> blocks = shape.getBlocks(world, player, x, y, z, face, Config.maxBlocks);

        isProcessing = true;
        try {
            for (MutableBlockPos pos : blocks) {
                Block block = world.getBlock(pos.x, pos.y, pos.z);

                boolean canTill = block == Blocks.grass || block == Blocks.dirt;
                if (!canTill) continue;

                // Check air above
                if (!world.isAirBlock(pos.x, pos.y + 1, pos.z)) continue;

                world.setBlock(pos.x, pos.y, pos.z, Blocks.farmland);
                world.playSoundEffect(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5,
                    Blocks.farmland.stepSound.getStepResourcePath(),
                    (Blocks.farmland.stepSound.getVolume() + 1.0F) / 2.0F,
                    Blocks.farmland.stepSound.getPitch() * 0.8F);

                if (Config.preventToolBreak && heldItem.getItemDamage() >= heldItem.getMaxDamage() - 1) {
                    break;
                }
                heldItem.damageItem(1, player);
                if (heldItem.stackSize <= 0) {
                    player.destroyCurrentEquippedItem();
                    break;
                }
            }
        } finally {
            isProcessing = false;
        }
    }

    private void handleCropHarvesting(World world, EntityPlayer player, int x, int y, int z,
                                       ForgeDirection face, PlayerData data) {
        Shape shape = ShapeRegistry.getShape(data.getCurrentShape());
        List<MutableBlockPos> blocks = shape.getBlocks(world, player, x, y, z, face, Config.maxBlocks);

        isProcessing = true;
        try {
            for (MutableBlockPos pos : blocks) {
                if (!isMatureCrop(world, pos.x, pos.y, pos.z)) continue;

                Block block = world.getBlock(pos.x, pos.y, pos.z);
                int meta = world.getBlockMetadata(pos.x, pos.y, pos.z);

                // Drop items
                block.dropBlockAsItem(world, pos.x, pos.y, pos.z, meta, 0);

                // Replant (reset to growth stage 0)
                world.setBlock(pos.x, pos.y, pos.z, block, 0, 3);
            }
        } finally {
            isProcessing = false;
        }
    }

    private boolean isMatureCrop(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        if (block instanceof BlockCrops) {
            int meta = world.getBlockMetadata(x, y, z);
            return meta >= 7; // Vanilla crops max growth = 7
        }
        return false;
    }
}
