# 书架置顶:实现方案

> 状态:**方案,未写代码。** 等真机验收结果再动手。

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

### 3. 入口:长按卡片

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

## 六、为什么现在不做

E 档剩下的两项(tint_gray 视觉确认、置顶)都需要**可靠的真机画面**。当前模拟器在 480×800 下仍会 SystemUI ANR,截图不可信。

在这种情况下写置顶,等于再堆一层未验证的改动。等真机截图回来后,先确认灰色是否需要调整,再一次性把置顶做完并验收。
