# AuReTim
Open-source portable psychomotor vigilance test for the Raspberry Pi

AuReTim uses Apache Maven as the software project management tool. After installing Maven, the program can be built using the command mvn install in the root directory of the source code. The command will download all required dependencies automatically, build the source code and save the finished packages in the folder “target”.

To install AuReTim on the Raspberry Pi, create the folder “/opt/auretim” and copy the generated JAR archive “AuReTim-VERSION.jar” (whereby “VERSION” is replaced with the current version number of AuReTim), the folder “lib” with its contents, as well as the file “auretim.sh” into it. After copying, grant executable rights to the file “auretim.sh”. Next, install a Java Runtime Environment (JRE) (version ≥ 8) into the folder “/opt/auretim/jre”. Finally, create a user named “auretim” without a password and grant sudoers-rights to it.

To automatically start AuReTim when booting the Raspberry Pi, append the line “sudo /opt/auretim/auretim.sh” to the file “/etc/rc.local”.

To set up the touchscreen as display and for input, the following files have to be copied to the Raspberry Pi:

•	/boot/cmdline.txt
•	/boot/config.txt
•	/etc/modules

These files are basically required to configure the ads7846 kernel module used as a driver for the touchscreen display, as well as the settings (e.g. screen orientation) for the display itself. If a different display is used, the configuration may differ.
