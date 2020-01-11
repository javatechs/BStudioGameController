# BStudioGameController
Bowler Studio Small Kat game controller

## What does it do?
This controller operates [SmallKat](https://hackaday.io/messages/room/279391).
It implements the [classic controller mapping](https://github.com/javatechs/WiiChuck#classic-controller-mapping), 
and is very loosely based on [the Wii controller](https://en.wikipedia.org/wiki/Wii_Classic_Controller). The controller communicates with [Bowler Studio](http://commonwealthrobotics.com/), a robotic development and device management platform. Bowler Studio interprets the controls and directs SmallKat movement.

## Running a script
You can run arbitrary scripts from the Android Game Controller. 
This requires [client code](#bowler-studio-client-code)
run on Bowler Studio to work.
The underlying strategy is to support transmission of multiple 'related' buffers from Game Controller (server) to Bowler Studio client. This implementation interprets the buffers as characters.


### Run Scripts UI
You can run a script from either local file system OR from the Internet.
In general, the command line is broken into tokens, separated by `,` (commas). 
- If loading from the Internet/HTTP, the first parameter is the url and the 2nd the file name.
- For local file system references the first parameter is the file name of the script.
- In both cases, the succeeding tokens are parameters to the script 

__Example command 1 - Groovy Dialog:__

`https://github.com/javatechs/BowlerDebug,src/main/groovy/DialogExample.groovy`

This example loads files from the Internet.

__Example command 2 - Working with local files:__

`file://movement.groovy,speak;Watch me sit!`

This example references a local file, movement.groovy. Since the file name doesn't start with '/'
the software searches for the movement.groovy in the default, home directory `~` of Bowler Studio.

__Example command 3 - Movement and speech:__

`https://github.com/javatechs/BowlerDebug,src/main/groovy/movement.groovy,wag;10;2,nod,no!`

In this case, the token that begins with `wag` is followed by `;10;2`.

`https://github.com/javatechs/BowlerDebug,src/main/groovy/movement.groovy,speak;Watch me sit!`

This demonstrates `BowlerKernel.speak()` method.

__Example command 4 - Sound effects:__

`https://github.com/javatechs/BowlerDebug,src/main/groovy/movement.groovy,playaudio;<someURL>`

`playaudio` plays a wav file from the internet or the file system.

__Other UI components:__
- Load/save Scripts file on options menu.
- Delete, move up/down (order) and 'Run Script' on individual scripts in script list.
- Add new script button associated with list.

### Bowler Studio Client Code
Bowler Studio client code launches a Bowler Studio script by calling ScriptingEngine methods. See [Run Scripts UI](#run-scripts-ui) for script text format.

- This [example](https://github.com/javatechs/BowlerDebug/blob/master/src/main/groovy/LoadGameController2.groovy) 
client code launches scripts from local file system or the Internet.

- The client calls either `gitScriptRun()` for Internet or `inlineFileScriptRun()` for local files. These methods are found in [ScriptingEngine](https://github.com/madhephaestus/bowler-script-kernel/blob/64238f1b7047d2c7fe41f6014d29088d5bae3c19/src/main/java/com/neuronrobotics/bowlerstudio/scripting/ScriptingEngine.java).


### Script Protocol
This code leverages the UDP protocol supplied in 
[SimplePacketComsJava](https://github.com/madhephaestus/SimplePacketComsJava).
The total message buffer size is 60 bytes long. The command ID is 1985.

For all message buffers, there are 2 bytes reserved for sequence number and multi-buffer flag.
Byte offset 0 is sequence number. It rolls over at 255 (0xff). Byte offset 1 reserved for flags, 
leaving 58 bytes remaining for the data buffer.

- Phase 1: Client polls server:
    - The client sets the (next) sequence number in the outgoing buffer. 
      This sequence number tells the server what sequence number to use to indicate 
      the client should read the next buffer.
    - This buffer is sent to the server.
- Phase 2: Server reads client poll:
    - The buffer contains 'next sequence number'.
- Phase 3: Server sends a response to client:
    - Zero outgoing buffer. Set sequence number to last sequence number.
    - If there is a buffer to send, transfer it to the outgoing buffer.
      Use next sequence number.
    - Set the sequence number to the one read during step inbound by 
    - The buffer contains a multi-buffer flag.
- Phase 4: Client reads server's response:
    - If the sequence number from server equals client sequence number, there is a message to process.
    - If the multi-buffer flag is set, append this buffer to the last.
    - If the multi-buffer flag is NOT set, process all accumulated buffers.
    - If this was a new buffer, 


## Additional Thanks
Portions use controlwear's [virtual-joystick-android](https://github.com/controlwear/virtual-joystick-android)
and [SimplePacketComsJava](https://github.com/madhephaestus/SimplePacketComsJava).

## License
```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
