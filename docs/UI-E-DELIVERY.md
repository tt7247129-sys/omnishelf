# E 档 UI 重构:交付说明

> 分支 `ui/redesign`。所有改动只在分支上,未合并 master。
> 安装包:`out/OmniShelf-ui-check.apk`(arm64,覆盖安装)。

## 一、做了什么

| 层 | 项 | 实现 | 验证 |
|---|---|---|---|
| T0 | 配色统一到墨绿 `#4A5D4E` | `AppState.STYLE_COLORS[1]` + 语义色令牌 + `colorPrimary/colorAccent` | ✅ 截图 |
| T0 | 清掉 XML 里的靛蓝 | 11 处硬编码 → `@color/omni_primary` | ✅ 静态审计 |
| T0 | 清掉 teal | `tint_gray` 资源 + `TintUtil.COLOR_TINT_GRAY` 常量 | ✅ 静态审计 |
| T0 | 对话框主题吃全局强调色 | `android:colorAccent` | ⚠️ 待真机 |
| T1 | 书架卡片:封面统一框、书名两行、内边距 | `browse_item_grid.xml` + 适配器 | ✅ 截图 |
| T1 | 打开即见最近在读 | 默认标签顺序 Recent 前置 | ✅ 截图 |
| T1 | 底部导航顺序与文案 | 同上 | ✅ 截图 |
| T2 | 排版默认值:标题上限、关断词、左对齐 | `BookCSS` 注入规则 | ✅ 截图 |
| T2 | 底栏 7 → 4 图标 | `document_footer.xml` | ✅ 截图 |
| T2 | 顶栏 5 → 4 图标 | `activity_horiziontal_view.xml` | ✅ 截图 |
| T2 | 状态栏跟随页面色 | 既有机制,配合 T0 生效 | ✅ 截图 |
| T3 | 封面进度角标 | 新增 `coverProgress` + 适配器绑定 | ✅ 截图 |
| T3 | 常驻时间电量 | `isShowTime/isShowBattery` 默认开 | ✅ 截图 |
| T3 | 置顶 | `Pinned` + 长按封面 | ⚠️ 待真机 |
| QA | 冒烟 / 截图矩阵 / 静态审计 | `scripts/` | ✅ |

## 二、提交

```
acf9cb9 pins: long press the cover, not the shared file menu
e0e2834 ui: a source-level audit, and the teal it found in Java
5d190f0 pins: storage and stable partition, without the menu entry
9c80d4d qa: stop the harness reporting green over a black screen
3273e9b ui(t3): the reading position sits on the cover
48d4643 ui(t2): the gear leaves the reading bar
48fb835 ui(t2): four controls at the foot, not seven
c681240 docs: t2 typography status
213ec19 ui(t1): open on Recent, not on everything you own
4fe13d7 ui(t1): even grid cells, two-line titles
761d396 ui(t0): one accent, defined once
```

## 二点五、收尾工程(2026-11-13)

| 项 | 状态 |
|---|---|
| 空书架:移除自动塞入的样书,空态提示可达并已验证 | ✅ |
| 误导性"搜索未完成"横幅:仅空库时出现,已退役 | ✅ |
| 设置入口:抽屉按钮在 Library 页常显 | ✅ |
| 无障碍:109 → 0,审计确认 | ✅ |
| 设置页:5 个运行时分段标题 | ✅ |
| 语言:新增 12 个语言包(见 `docs/LOCALE-PLAN.md`) | ⏳ 落地中 |
| 包体积:release + R8 + 资源收缩 49M → 36M | ✅ |
| MuPDF 以 -Os 重编译(体积优先) | ⏳ 进行中 |
| monkey 500 事件压测 | ✅ 零崩溃 |

## 三、已知限制(不隐藏)

**1. 面板打开时正文最后一行被遮**
面板浮在页面上,不改变正文高度。修它要动重排,`docs/UI-PLAN.md` 里有分三步的做法,本轮未做。

**2. 空书架没有引导文案**
试过一版,判据错了(用了 adapter 行数,而列表始终有分组标题行),已回退。正确判据要基于 `AppData` 的真实书籍数。

**3. 书架里已删除的书仍会显示(已修)**

书的记录留在数据库里,每次扫描又读回来。现在扫描结束时会问一次文件系统,
把不存在且不是云/SAF 的记录删除。修改:`SearchAllBooksWorker`。

注意:应用会把打开过的书**复制**到自己的 `Android/data/.../TempDownloads`,
所以"删掉原文件"不等于"从书架移除"——这是现有设计,不是 bug。

**3. 无障碍名称缺口 109 处**

`scripts/ui-audit.sh` 统计:45 个布局里还有 109 个 `ImageView` 没有描述、也没标为装饰。
按数量排前的是 `dialog_fonts.xml`(12)、`dialog_prefs.xml`(10)、`document_title_bar.xml`(8)。
这不是一轮能清完的,建议作为独立的技术债任务,按文件逐个处理。

**4. 广告页残留**
`ads.xml` 已删除(零引用)。`showRewardVideo` 那一块仍在 `activity_horiziontal_view.xml` 里,运行时因 `isRewardLoaded()==false` 永不显示,但代码与布局还在,应单独一轮整块删除。

**5. 窄屏标签截断**
480dp 宽度下底栏标签会截断("Bookm…")。手机正常宽度(≥360dp)下不出现,但 5 寸以下设备会有。

## 四、验证方式

真机清单:`docs/UI-VERIFY-REQUEST.md`(四节:书架 / 阅读页 / 对话框 / 置顶)。

**没有设备时不要合并到 master。** 分支上的改动虽然逐个编译通过、静态审计通过,但「自动通过」不等于「人看见是对的」——本项目已经吃过两次假绿灯的亏。

## 五、如果只能保留一半

按性价比排序,建议优先保留:

1. **T0 配色** — 观感差异最大,零功能风险
2. **T2 排版默认值** — 决定"打开一本书像不像书"
3. **T1 卡片** — 第一眼质量
4. **T2 工具栏收敛** — 减少认知负担,但改动面大
5. **T3 角标/时间电量/置顶** — 锦上添花
