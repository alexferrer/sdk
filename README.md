# SDK
Recon SDK related files

Windows 8 adb drivers and latest Lollipop Ubuntu/Windows executables:
http://www.reconinstruments.com/wp-content/recon-sdk/adb.zip

For Eclipse projects (not needed for gradle projects):

First time only to get the SDK Add-On:
- Open the Android SDK Manager
- Click on Tools
- Select Manage Add-Ons site
- Select User Defined Sites
- Select New and add: http://www.reconinstruments.com/wp-content/recon-sdk/repository.xml
- Click close
- Scroll Down to API-16 and check "Recon Instruments"
- Select to download the add-on

- In the project properties, under Android, select "Recon Instruments SDK"

# Setting up an emulator

Copy Tools/devices.xml into your local .android directory

Windows: 
    C:\Users\[User Name]\.android
Linux, Max OS: 
    ~/.android

if you have previous user defined device definitions you want to keep, you'll have to manually merge the device tag into your existing devices.xml

from command line, in your android SDK tools/ directory, run
    android avd

(Note: the android device manager included with Android Studio or Intellij IDEA will not be able to configure dynamic hardware controls. Creating a skin overcomes this and a DPAD is accessible from the "extended controls" of the emulator.)

go to 'Device Definitions', select 'Recon Jet' (2in, 428*240px small mdpi screen) and press Create AVD

for Target, select 'Android 4.1.2 - API Level 16'
for CPU, select 'Intel Atom (x86)'
for skin, select 'Skin with dynamic hardware controls'

press 'Ok'
close the Android Virtual Device Manager

From your IDE, you can now start an emulator from your IDEs android virtual device manager, or from the device chooser dialog when starting an app

Currently this uses a generic android system image and as such will not be exactly equivalent to testing your apps on a real jet device.
ActivityStatusDemo, BluetoothLEDemo, ConnectivityDemo, DemoCompass, ExternalSensorDemo, GlanceDemo, MetricsDemo and MyFirstReconApp all require a Recon device to run.
ReconUIExamples, NotificationDemo and CameraSample are able to run on emulators.