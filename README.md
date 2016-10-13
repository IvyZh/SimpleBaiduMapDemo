# SimpleBaiduMapDemo
简单的Baidu地图示例

* 先要在Baidu开放平台创建应用
	- 发布版SHA1
		
		cd .android

		keytool -list -v -keystore debug.keystore

		密钥：android


- Demo中实现的功能

		地图类型    			-OK
		实时交通图			-OK
		百度城市热力图		-OK
		地图控制和手势		-OK
		标注覆盖物			-OK
		几何图形覆盖物		-OK
		文字覆盖物			-OK
		弹出窗覆盖物			-OK
		地形图图层
		热力图功能			-OK
		检索结果覆盖物
		OpenGL绘制功能
		TextureMapView渲染
		瓦片图层
		设置地图区域边界
		设置地图显示范围


- 加入Android定位SDK

		No direct method <init>(Lcom/baidu/lbsapi/auth/LBSAuthManager;Landroid/os/Lo