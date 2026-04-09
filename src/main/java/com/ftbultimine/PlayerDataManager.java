package com.ftbultimine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.ftbultimine.shape.Shape;
import com.ftbultimine.shape.ShapeType;

import net.minecraft.entity.player.EntityPlayer;

public class PlayerDataManager {

    private static final Map<UUID, PlayerData> dataMap = new HashMap<UUID, PlayerData>();

    public static void clear() {
        dataMap.clear();
    }

    public static PlayerData get(EntityPlayer player) {
        UUID id = player.getUniqueID();
        PlayerData data = dataMap.get(id);
        if (data == null) {
            data = new PlayerData();
            dataMap.put(id, data);
        }
        return data;
    }

    public static void remove(EntityPlayer player) {
        dataMap.remove(player.getUniqueID());
    }

    public static class PlayerData {

        private boolean ultimineActive = false;
        private ShapeType currentShape = ShapeType.SHAPELESS;
        private long lastUltimineTime = 0;

        public boolean isUltimineActive() {
            return ultimineActive;
        }

        public void setUltimineActive(boolean active) {
            this.ultimineActive = active;
        }

        public ShapeType getCurrentShape() {
            return currentShape;
        }

        public void setCurrentShape(ShapeType shape) {
            this.currentShape = shape;
        }

        public void cycleShape(boolean forward) {
            ShapeType[] types = ShapeType.values();
            int idx = currentShape.ordinal();
            if (forward) {
                idx = (idx + 1) % types.length;
            } else {
                idx = (idx - 1 + types.length) % types.length;
            }
            currentShape = types[idx];
        }

        public long getLastUltimineTime() {
            return lastUltimineTime;
        }

        public void setLastUltimineTime(long time) {
            this.lastUltimineTime = time;
        }

        public boolean isOnCooldown(long currentTime) {
            if (Config.ultimineCooldown <= 0) return false;
            return (currentTime - lastUltimineTime) < Config.ultimineCooldown;
        }
    }
}
