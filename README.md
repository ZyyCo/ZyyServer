# ZyyLoader

This repository is a **personal experimental project**.

> 菜鸡写的，调试味贼重，代码混乱，没写完，不建议用于任何严肃场合。

---

### 🤡 Status

This project is:
- 🧪 **Incomplete** and still under messy development
- A prototype / proof-of-concept
- Not actively maintained
- Not intended for production use
- Subject to cleanup, refactor, or total removal at any time

---

### Forge Bukkit 混合端（开发测试版）
- **版本：** Minecraft 1.16.5
- 已在 java11 测试环境验证的插件（Plugins 10）：
  LuckPerms, Vault, PlaceholderAPI, ProtocolLib, Multiverse-Core, CHILib, Citizens, CHT, CHIEin ector, MythicMobs...
- 当前仅为开发测试版，可能存在不兼容或不稳定的情况(也仅仅是能跑而已!!)
- 仅在个人学习/测试环境使用，很多插件仅仅是能跑而已 不建议用于生产环境
---

### 📌 Purpose


The code explores:
- Java SPI isolation and service loading behavior
- ClassLoader manipulation (incl. agents, ASM, Mixin)
- Forge mod loading startup internals

This is **not a full solution**, just part of my learning and exploration.

---

## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](./LICENSE) file for details.

---

### 🐕 Why is this public?

> Just using GitHub as a **cloud backup**.  
> No commercial intent. No promises.  
> Not a proper release. Not meant for actual use.

---

### 启动方式 🚀
> agent挂载方式启动，需两个文件：`zyy-common.jar` 和 `zyy-launcher.jar`（文件名按编译后的名称为准）。  
> `-javaagent` 用于挂载 ZyyLoader 的增强功能。

```bash
java -javaagent:zyy-common.jar -jar zyy-launcher.jar
``` 

---

本项目公开只是作为「个人云备份」，无商业意图，也不代表正式发布。  
菜鸡式代码，没测试过，天煞式的调试信息。
不太会修bug，代码写多了脑子烧了 🤡
QQ1190260