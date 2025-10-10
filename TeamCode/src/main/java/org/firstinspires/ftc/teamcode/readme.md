## TeamCode Module

OpModes and non-Android code moved to the TeamCodeLib module. This allows them to be loaded by
the https://github.com/jaxley/virtual_robot simulator. The project structure is the same but an 
android libary (.aar file) gets built to TeamCodeLib/build/outputs that virtual_robot can load.