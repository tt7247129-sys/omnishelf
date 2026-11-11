# 书架置顶:实现方案

> 状态:**数据与排序已实现,入口未接**(见 3. 的风险)。

## 一、先澄清一件事:分组已经存在

排查发现应用早就有标签体系:

- `Tags2.getAllTags()` / `getAllTagsWithCount()` — 标签的增删查
- `Dialogs.addTagsDialog()` — 给一本书加标签的对话框
- `FavoritesFragment2:372` — 收藏页按标签**分组展示**,带每组的数量

也就是说"按类型分组"这件事,用户今天就能做:给书加标签 → 在收藏页按标签浏览。

**缺的不是分组,是"置顶"**:把常看的几本钉在书架最上面,不管当前怎么排序。

## 二、置顶要动三处

### 1. 存储:不要动数据库

书架的数据在 greenDAO(`AppDB`)里。加一个"置顶"字段意味着 **schema 迁移**——迁移写错会让老用户的书库读不出来,这是本项目最不能承受的故障。

改用旁路存储:一个单独的偏好文件,只存路径集合。

```java
// Pinned.java (new)
private static final String KEY = "pinnedPaths";   // JSON array of paths
public static Set<String> getAll();
public static boolean isPinned(String path);
public static void toggle(String path, boolean pinned);
```

理由:路径是书库的天然主键;丢失它的后果只是"置顶清空",不影响书库本身。**故障模式是降级的,不是破坏性的。**

### 2. 排序:在结果出库之后重排

排序发生在 `AppDB.searchBy(txt, SORT_BY, order, …)` 里,是 SQL 层的 `ORDER BY`。
不改 SQL,改为在拿到列表后做一次**稳定分区**:

```java
List<FileMeta> all = AppDB.get().searchBy(...);
List<FileMeta> pinned = new ArrayList<>();
List<FileMeta> rest = new ArrayList<>();
for (FileMeta f : all) (Pinned.isPinned(f.getPath()) ? pinned : rest).add(f);
all.clear(); all.addAll(pinned); all.addAll(rest);
```

稳定分区的好处:置顶内部仍按用户选的排序,置顶不改变其余顺序,也不会出现"点了置顶整页跳一下"。

**改一处即可**:`SearchFragment2` 里把结果交给适配器之前。

### 3. 入口:溢出菜单 —— 这一步有真实危险

书卡的 ⋮ 与长按都进入 `ShareDialog`(`DefaultListeners:405`)。它的点击回调是**按下标递进**的:

```java
int i = 0;
if (!isImageOrEpub(file)) { if (which == i++) { convert EPUB } if (which == i++) { convert PDF } }
if (which == i++) { openWith } else if (which == i++) { sendFile }
else if (canDelete && which == i++) { dialogDelete }        // ← 删除
else if (isShowInfo && which == i++) { showFileInfo }
```

条目是**有条件添加**的(图片/EPUB 少两项、`canDelete`、`isShowInfo`),所以插入"置顶"必须让**渲染时的下标与点击时的自增序列完全一致**。错一位,"置顶"就会触发**删除书籍**。

在没有真机把每个分支都点一遍的情况下,这个改动不能做。已经在 `Pinned` 类里做完了存储与排序,入口等设备可用后再接。

### 3b. 入口(原始设想):长按卡片

长按书卡已有上下文菜单(收藏/重命名/删除等)。在同一个菜单里加一项:

- 未置顶 → 「置顶」
- 已置顶 → 「取消置顶」

视觉标记:封面左上角一个**小图钉**,与右下角的进度角标对角放置,互不遮挡。

## 三、风险与取舍

| 风险 | 处理 |
|---|---|
| 排序重排影响网格/列表/搜索三种模式 | 重排放在结果出口,三种模式共用;每模式截图验证 |
| 置顶项很多时把正常书挤下去 | 上限 20 本,超出时提示"置顶已满" |
| 路径失效(文件被移动) | 读取时静默跳过,不报错、不清空 |
| 与"最近"页冲突 | 置顶**只在 Library 生效**,Recent 保持时间序 |

## 四、验证计划

1. 编译 + 冒烟 9 项(现有脚本)
2. 步骤:置顶一本 → 切三种排序 → 每次置顶项都在首位 → 取消置顶 → 回到原位
3. 重启应用 → 置顶仍在
4. 移动文件 → 置顶项消失但不崩溃

## 五、工作量

约 4–6 小时:存储类 1h、重排 1h、菜单项 1–2h、验证 2h。

## 六、现在完成到哪一步

已完成(编译通过,无行为变化):

- `com.foobnix.model.Pinned` — 路径集合的存取、`partition()` 稳定分区
- `SearchFragment2` 在查询结果注入前调用 `Pinned.partition(...)`

未做:**切换入口**。原因见 3:溢出菜单的下标自增逻辑错一位就会删书。

## 七、恢复施工的条件

任意一个成立即可继续:

1. 有一台可用的真机/模拟器,能逐个分支点一遍溢出菜单
2. 或者把入口从 `ShareDialog` 换成**独立的长按卡片菜单**(新建一个只含三项的对话框:置顶/书签/信息),不碰现有下标逻辑

第 2 条其实更安全,只是多写一个对话框。
