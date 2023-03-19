package me.florixak.uhcrun.inventory;


import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.inventory.inventories.CustomGUI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventoryManager {

    private UHCRun plugin;

    private Map<String, AbstractInventory> inventories;

    public InventoryManager() {
        inventories = new HashMap<>();
    }

    public void onEnable(UHCRun plugin) {
        this.plugin = plugin;

        loadCustomMenus();

        inventories.values().forEach(AbstractInventory::onEnable);

        plugin.getServer().getPluginManager().registerEvents(new InventoryListener(), plugin);
    }

    private void createFile(String path, String name) {
        File file = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + path, name + ".yml");
        try {
            file.createNewFile();
            InputStream inputStream = this.plugin.getResource(name + ".yml");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            OutputStream outputStream = new FileOutputStream(file);
            outputStream.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    private void loadMenus(String path) {
        File[] yamlFiles = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + path).listFiles((dir, name) -> name.toLowerCase().contains("inv"));
        if (yamlFiles == null) return;
        for (File file : yamlFiles) {
            String name = file.getName().replace(".yml", "");
            if (inventories.containsKey(name)) {
                plugin.getLogger().warning("Inventory with name '" + file.getName() + "' already exists, skipping duplicate..");
                continue;
            }

            CustomGUI customGUI;
            try {
                customGUI = new CustomGUI(plugin, YamlConfiguration.loadConfiguration(file));
            } catch (Exception e) {
                plugin.getLogger().severe("Could not load file '" + name + "' (YAML error).");
                e.printStackTrace();
                continue;
            }

            inventories.put(name, customGUI);
            plugin.getLogger().info("Loaded custom menu '" + name + "'.");
        }
    }

    private void loadCustomMenus() {
        createFile("", "statistics-inv");
        createFile("", "perks-inv");
        createFile("", "kits-inv");
        createFile("", "vote-inv");

        loadMenus("");
    }

    /*private void loadCustomMenus() {

        // CREATE
        File file0 = new File(plugin.getDataFolder().getAbsolutePath() + File.separator, "kits-inv.yml");
        try {
            file0.createNewFile();
            InputStream inputStream0 = this.plugin.getResource("kits-inv.yml");
            byte[] buffer0 = new byte[inputStream0.available()];
            inputStream0.read(buffer0);
            OutputStream outputStream0 = new FileOutputStream(file0);
            outputStream0.write(buffer0);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        File file1 = new File(plugin.getDataFolder().getAbsolutePath() + File.separator, "statistics-inv.yml");
        try {
            file1.createNewFile();
            InputStream inputStream1 = this.plugin.getResource("statistics-inv.yml");
            byte[] buffer1 = new byte[inputStream1.available()];
            inputStream1.read(buffer1);
            OutputStream outputStream1 = new FileOutputStream(file1);
            outputStream1.write(buffer1);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        File file2 = new File(plugin.getDataFolder().getAbsolutePath() + File.separator, "vote-inv.yml");
        try {
            file2.createNewFile();
            InputStream inputStream2 = this.plugin.getResource("vote-inv.yml");
            byte[] buffer2 = new byte[inputStream2.available()];
            inputStream2.read(buffer2);
            OutputStream outputStream2 = new FileOutputStream(file2);
            outputStream2.write(buffer2);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        File file3 = new File(plugin.getDataFolder().getAbsolutePath() + File.separator, "perks-inv.yml");
        try {
            file3.createNewFile();
            InputStream inputStream3 = this.plugin.getResource("perks-inv.yml");
            byte[] buffer3 = new byte[inputStream3.available()];
            inputStream3.read(buffer3);
            OutputStream outputStream3 = new FileOutputStream(file3);
            outputStream3.write(buffer3);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }


        // LOAD
        File[] yamlFiles1 = new File(plugin.getDataFolder().getAbsolutePath() + File.separator).listFiles((dir, name) -> name.toLowerCase().contains("inv"));
        if (yamlFiles1 == null) return;
        for (File file : yamlFiles1) {
            String name = file.getName().replace(".yml", "");
            if (inventories.containsKey(name)) {
                plugin.getLogger().warning("Inventory with name '" + file.getName() + "' already exists, skipping duplicate...");
                continue;
            }

            CustomGUI customGUI;
            try {
                customGUI = new CustomGUI(plugin, YamlConfiguration.loadConfiguration(file));
            } catch (Exception e) {
                plugin.getLogger().severe("Could not load file '" + name + "' (YAML error).");
                e.printStackTrace();
                continue;
            }

            inventories.put(name, customGUI);
            plugin.getLogger().info("Inventory '" + name + "' loaded. ✔");
        }
    }*/

    public void addInventory(String key, AbstractInventory inventory) {
        inventories.put(key, inventory);
    }

    public Map<String, AbstractInventory> getInventories() {
        return inventories;
    }

    public AbstractInventory getInventory(String key) {
        return inventories.get(key);
    }

    public void onDisable() {
        inventories.values().forEach(abstractInventory -> {
            for (UUID uuid : abstractInventory.getOpenInventories()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) player.closeInventory();
            }
            abstractInventory.getOpenInventories().clear();
        });
        inventories.clear();
    }
}
