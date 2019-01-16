# FluidTab
一个底部导航栏的库

效果图：<br>
![在这里插入图片描述](https://raw.githubusercontent.com/qixuefeng/FluidTab/master/pic/fluidtab.gif)

# 依赖添加

第一步：将其添加到存储库末尾的根build.gradle中：

```
    allprojects {
    	repositories {
    		maven { url 'https://jitpack.io' }
    	}
    }
```

第二步：添加到依赖

```
dependencies {
         implementation 'com.github.qixuefeng:FluidTab:1.0'
}
```
   当前版本：
[![](https://jitpack.io/v/qixuefeng/FluidTab.svg)](https://jitpack.io/#qixuefeng/FluidTab)

# 使用步骤
将FluidTab写入XML文件中，由于要显示fragment，所以此处添加一个FrameLayout显示fragment

```
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <FrameLayout
        android:id="@+id/content"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>

    <com.qxf.library.FluidTab
        android:layout_gravity="bottom"
        android:id="@+id/fluidTab"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />

</FrameLayout>
```
Tab是用来储存每一个标签信息的类，可以储存的数据有icon、标题、需要被显示的fragment这三个元素。
```
Tab[] tabs = new Tab[4];
tabs[0] = new Tab(R.drawable.ic_home, "主页", OneFragment.newInstance("fragment1"));
tabs[1] = new Tab(R.drawable.ic_record, "记录", OneFragment.newInstance("fragment2"));
tabs[2] = new Tab(R.drawable.ic_shop, "购物", OneFragment.newInstance("fragment3"));
tabs[3] = new Tab(R.drawable.ic_mine, "我的", OneFragment.newInstance("fragment4"));
```
 将FluidTab和Tab信息绑定
> 注：以下顺序不能交换

```
fluidTab.involve(getSupportFragmentManager(), R.id.content);
fluidTab.setTabs(tabs);
```
当你进行到这一步时，FluidTab已经可以正常工作，内部会自动调控点击事件，无需额外实现点击事件。

# FluidTab方法一览
|方法|	描述	|
|-----|-----|
|setBgColor(int backgroundColor)|修改背景颜色|
|setTabColor(int tabColor)|修改Tab颜色|
|setTextColor(int textColor)|修改文字颜色|
|setOnItemClickListener(FluidTab.onItemClickListener l)|设置点击监听|
|switchFragment(int currentItem)|切换到某一个Fragment|
|defaultItem(int i)|设置默认打开页数|
|setTabs(Tab[] tabs)|赋予Tab数据(Fragment数据)|
|involve(FragmentManager fragmentManager, int id)|注入FragmentManager和id<br>该ID为显示fragment的容器ID|

