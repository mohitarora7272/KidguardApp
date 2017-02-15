1. You need to have internet conenction for the application.
2. You need to on location service for the app.
3. When you will run the app the app will generate text file in stoarge with name Data.txt.You can see output result by open that file.

How to create APK from source code:

1. First you need to install Android studio.
2. If Android studio already installed then import the project.
3. Now you need to run the application,you can run the app on emulator(virtual device) or real device.

How to Run on an Emulator

1. Launch the Android Virtual Device Manager by selecting Tools > Android > AVD Manager, or by clicking the AVD Manager icon  in the toolbar.
2. In the Your Virtual Devices screen, click Create Virtual Device.
3. In the Select Hardware screen, select a phone device, such as Nexus 6, and then click Next.
4. In the System Image screen, choose the desired system image for the AVD and click Next.
5. If you don't have a particular system image installed, you can get it by clicking the download link.
6. Verify the configuration settings (for your first AVD, leave all the settings as they are), and then click Finish.

How to Run on Device

1. Connect your device to your development machine with a USB cable. If you're developing on Windows, you might need to install the appropriate USB driver for your device. For help installing drivers, see the OEM USB Drivers document.
2. Enable USB debugging on your device by going to Settings > Developer options.
Note: On Android 4.2 and newer, Developer options is hidden by default. To make it available, go to Settings > About phone and tap Build number seven times. Return to the previous screen to find Developer options.

Reagrding Base URl and API Token

1. If you want to change base url of the app then you need to change it on "Constant" class.It's under "interface" package 
example:String ROOT = "http://192.168.0.10:3000/";
2. As you know API token is appened with all API's but for now i have used static API token.So you need to change that token and it's also in "Constant" class
example: String API_TOKEN = "c0u0NAZSCsMaahh4Z74J7haYJGSXWs7MoP0WfAXMI1EWpKER9Spqi2SG2ORD";


Regarding Device Token

1. When you will run the app then you will able to see device token in log and log is used in "GCMIntentService" class.
example: Log.e(TAG, "registrationId>>>>" + registrationId);
After getting device token you need to use that token in backend for notification.
When you will use that device token in backend then you will able to get notification and after that you will able to sync SMS,Calls etc.







