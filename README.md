## What does this do?
This project aims to replace or at least complement [`TCP Gecko.NET`](https://github.com/Chadderz121/tcp-gecko-dotnet) as platform independent Wii U RAM debugger and cheat code manager. It is developed in `Java` so you need to have [`Java`](https://www.java.com/en/download/) installed.

## Where do I post about problems I'm having?
Please post them [here](https://github.com/BullyWiiPlaza/JGeckoU/issues).

## How do I run this tool?
You need to download `JGecko U.jar` [here](https://github.com/BullyWiiPlaza/JGeckoU/blob/master/JGecko%20U.jar?raw=true). Double-click it or type `java -jar "JGecko U.jar"` on the command line.

## What are the necessary steps to connect?
Download the [TCP Gecko Installer](https://gbatemp.net/attachments/tcpgecko-rar.49875/?temp_hash=12f1648bcb0bfc08cd5e10534883b198). Extract it to your SD Card into the folder `E:\wiiu\apps\TCPGecko`.
Proceed to run the [`Homebrew Launcher`](https://github.com/dimok789/homebrew_launcher) by visiting [this](http://wj44.ml/launcher/) URL with your Wii U. Finally, load the `TCP Gecko Installer` app and press X to install the real time cheat code handler.

## What does the modified `TCP Gecko Installer` do?
It allows you to write cheat codes into the memory starting at address 0x10015000. Read [CosmoCortney's post](http://gbatemp.net/threads/wip-code-types-and-ppc-code-handler-for-cheat-codes.414834) for more information.

## How do I compile this project?
Since this is an [`IntelliJ IDEA`](https://www.jetbrains.com/idea/) project you have to download IntelliJ IDEA community edition. Then you can clone a Github repository by clicking on `File -> New -> Project from Version Control -> GitHub`.

## Where can I discuss the development of this application?
I setup a [GBATemp topic](http://gbatemp.net/threads/development-of-new-wii-u-ram-debugger.413477/) dedicated to this.

## Who gets credit for all this?
[BullyWiiPlaza](http://gbatemp.net/members/bullywiiplaza.352259/) for creating and programming JGecko U  
[The libwiiu team](https://github.com/wiiudev/libwiiu) for their kernel and browser exploit  
[The pyGecko team](https://github.com/wiiudev/pyGecko) for their pyGecko installer  
[CosmoCortney](https://github.com/CosmoCortney/PPC-Cheat-Code-Handler/blob/master/PPC_Code.txt) for the code handler assembly code  
[wj44](https://gbatemp.net/members/wj44.367152/) for helping to integrate the PPC code handler into an `elf`  
[Gudenaurock](http://gbatemp.net/members/gudenaurock.257499/) and [Maschell](http://gbatemp.net/members/maschell.128610/) for code improvement suggestions  
[NWPlayer123](http://gbatemp.net/members/nwplayer123.297271/) for help with the Wii U connector Java client code  
[Chadderz](https://github.com/Chadderz121) for TCP Gecko.NET