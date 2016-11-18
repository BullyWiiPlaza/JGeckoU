## What does this do?
This project aims to replace or at least complement [`TCP Gecko.NET`](https://github.com/Chadderz121/tcp-gecko-dotnet) as platform independent Wii U RAM debugger and cheat code manager. It is developed in `Java` so you need to have [`Java`](https://www.java.com/en/download/) installed.

## Where do I post about problems I'm having?
Please post them [here](https://github.com/BullyWiiPlaza/JGeckoU/issues).

## How do I run this tool?
You need to download `JGecko U.jar` [here](https://github.com/BullyWiiPlaza/JGeckoU/blob/master/JGecko%20U.jar?raw=true). Double-click it or type `java -jar "JGecko U.jar"` on the command line.

## What are the necessary steps to connect?
Download the [TCP Gecko Installer](https://github.com/BullyWiiPlaza/tcpgecko/blob/master/tcpgecko.elf?raw=true). Extract it to your SD Card into the folder `E:\wiiu\apps\TCPGecko`. Alternatively, also download [`CosmoCortney's cheat code handler`](http://cosmocortney.ddns.net/wiiu_tools/codehandler.bin) and place it in ths same directory (recommended).
Proceed to run the [`Homebrew Launcher`](https://github.com/dimok789/homebrew_launcher) by visiting [this](http://wj44.ml/launcher/) URL with your Wii U. Finally, load the `TCP Gecko Installer` app. Press X to install the real time cheat code handler or A to start without.

## How do I compile this project?
Since this is an [`IntelliJ IDEA`](https://www.jetbrains.com/idea/) project you have to download IntelliJ IDEA community edition. Then you can clone a Github repository by clicking on `File -> New -> Project from Version Control -> GitHub`.

## Where can I discuss the development of this application?
I setup a [GBATemp topic](http://gbatemp.net/threads/development-of-new-wii-u-ram-debugger.413477/) dedicated to this.

## Where do I get help/support?
[This](http://gbatemp.net/threads/post-your-wiiu-cheat-codes-here.395443/) is a good thread.

## Who gets credit for all this?
[BullyWiiPlaza](http://gbatemp.net/members/bullywiiplaza.352259/) for creating and programming `JGecko U`  
[The libwiiu team](https://github.com/wiiudev/libwiiu) for the kernel and browser exploits  
[The pyGecko team](https://github.com/wiiudev/pyGecko) for the original `TCP Gecko Installer`  
[CosmoCortney](https://github.com/CosmoCortney/PPC-Cheat-Code-Handler/blob/master/PPC_Code.txt) for the code handler  
[wj44](https://gbatemp.net/members/wj44.367152/) for `TCPGecko.elf` development  
[Gudenaurock](http://gbatemp.net/members/gudenaurock.257499/) for Wii U connector Java client code/code improvement suggestions  
[NWPlayer123](http://gbatemp.net/members/nwplayer123.297271/) for help with the Wii U connector Java client code  
[Chadderz](https://github.com/Chadderz121) for `TCP Gecko.NET`