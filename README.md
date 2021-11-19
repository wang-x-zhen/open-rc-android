# open-rc-android

主要功能
## 1 通道映射设置 
## 2 最多支持7通道 
## 3 通道微调
## 4 通道可以设置 PWM模式：直驱（一般控制MOSFET管）、舵机
## 5 通道反转

待开发混控、通道回中自定义...
欢迎大家有好的需求尽量提出来，我会抽时间实现

使用步骤
1.手机新建热点，这个热点是用来接收机来连接的，控制端和接收机基于局域网UDP通信。
2.接收机烧录进 open-rc-esp代码 
3.给接收机供电，接受机会自动连接此网络
4.在接收机设置页选择要控制的接收机（可以多选，同时控制）
![Screenshot_1637312138](https://user-images.githubusercontent.com/5453963/142594431-38a08f9f-4fb7-4371-b794-743396131ebe.png)
![Screenshot_1637312269](https://user-images.githubusercontent.com/5453963/142594672-eec34da3-b504-4a14-b809-ed4d76623f46.png)
![Screenshot_1637312223](https://user-images.githubusercontent.com/5453963/142594583-b7c1eca7-2707-4074-875e-f2fb33132a2e.png)
[APK](app/release/app-release.apk)
