package com.example.addon;

import com.example.addon.modules.AHSeller;
import com.example.addon.modules.AnchorMacro;
import com.example.addon.modules.AntiTrap;
import com.example.addon.modules.AutoFillItem;
import com.example.addon.modules.AutoFirework;
import com.example.addon.modules.AutoInvTotem;
import com.example.addon.modules.AutoJoin;
import com.example.addon.modules.AutoLogin;
import com.example.addon.modules.AutoMine;
import com.example.addon.modules.AutoOrder;
import com.example.addon.modules.AutoSell;
import com.example.addon.modules.AutoWalk;
import com.example.addon.modules.BlockAttackEntity;
import com.example.addon.modules.ChunkFinder;
import com.example.addon.modules.ClusterFinder;
import com.example.addon.modules.CommandPlayerList;
import com.example.addon.modules.CriticalKillAura;
import com.example.addon.modules.CrystalLegit;
import com.example.addon.modules.CustomCrosshair;
import com.example.addon.modules.DripstoneESP;
import com.example.addon.modules.DropHotbar;
import com.example.addon.modules.DropItem;
import com.example.addon.modules.DropItemGui;
import com.example.addon.modules.EntityAlert;
import com.example.addon.modules.FakeNotifier;
import com.example.addon.modules.FakeScoreboard;
import com.example.addon.modules.FreezePlayer;
import com.example.addon.modules.HighPing;
import com.example.addon.modules.ItemSwap;
import com.example.addon.modules.KeyPearl;
import com.example.addon.modules.RTPFinderBase;
import com.example.addon.modules.SafeWalk;
import com.example.addon.modules.SetupHotbar;
import com.example.addon.modules.ShopBuyer;
import com.example.addon.modules.SpawnerProtect;
import com.example.addon.modules.StudentFreeCam;
import com.example.addon.modules.TNTMinecartMacro;
import com.example.addon.modules.TunnelFinderBase;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

public class AddonTemplate extends MeteorAddon {
   public static final Logger LOG = LogUtils.getLogger();
   public static final Category Student = new Category("Student");
   public static final Category Student_pvp = new Category("Student pvp");
   public static final Category Student_esp = new Category("Student esp");

   public void onInitialize() {
      LOG.info("Student Addon");
      Modules.get().add(new EntityAlert());
      Modules.get().add(new ItemSwap());
      Modules.get().add(new AutoFillItem());
      Modules.get().add(new CommandPlayerList());
      Modules.get().add(new RTPFinderBase());
      Modules.get().add(new BlockAttackEntity());
      Modules.get().add(new ShopBuyer());
      Modules.get().add(new AHSeller());
      Modules.get().add(new AntiTrap());
      Modules.get().add(new CrystalLegit());
      Modules.get().add(new AutoOrder());
      Modules.get().add(new HighPing());
      Modules.get().add(new FakeNotifier());
      Modules.get().add(new SetupHotbar());
      Modules.get().add(new DropHotbar());
      Modules.get().add(new AutoLogin());
      Modules.get().add(new SpawnerProtect());
      Modules.get().add(new CriticalKillAura());
      Modules.get().add(new KeyPearl());
      Modules.get().add(new AutoSell());
      Modules.get().add(new AnchorMacro());
      Modules.get().add(new StudentFreeCam());
      Modules.get().add(new AutoInvTotem());
      Modules.get().add(new FakeScoreboard());
      Modules.get().add(new AutoWalk());
      Modules.get().add(new AutoMine());
      Modules.get().add(new SafeWalk());
      Modules.get().add(new AutoFirework());
      Modules.get().add(new TNTMinecartMacro());
      Modules.get().add(new ClusterFinder());
      Modules.get().add(new DropItemGui());
      Modules.get().add(new TunnelFinderBase());
      Modules.get().add(new ChunkFinder());
      Modules.get().add(new DripstoneESP());
      Modules.get().add(new CustomCrosshair());
      Modules.get().add(new AutoJoin());
      Modules.get().add(new DropItem());
      Modules.get().add(new FreezePlayer());
   }

   public void onRegisterCategories() {
      Modules.registerCategory(Student);
      Modules.registerCategory(Student_pvp);
      Modules.registerCategory(Student_esp);
   }

   public String getPackage() {
      return "com.example.addon";
   }

   public GithubRepo getRepo() {
      return new GithubRepo("MeteorDevelopment", "meteor-addon-template");
   }
}
