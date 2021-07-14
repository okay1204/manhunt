# Manhunt (Spigot plugin for version 1.17.1)
Have you ever seen Dream's famous Minecraft manhunt videos and wanted to play it for yourself?

This spigot plugin provides the tools needed to play manhunt for yourself. One player of your choosing will be set as the runner, and every else will be a hunter. The hunters will be given a compass that tracks the runner. The runner wins if s/he beats the game, whilst the hunters are trying to stop the runner.
## Installation
Simply download the .jar file from the releases page and drag and drop it into your Spigot server's plugin folder.

## Setup
Setup is quite easy:
1. First run: `/manhunt track <username>`, where username is the username of the runner. _Example: /manhunt track AppleMan123_
2. **Optional**: Configure any settings you wish to change using `/manhunt settings <setting> <on|off>`. _More information below._
3. Next, make sure everyone is ready to start the game and run `/manhunt start`. This will start the game and clear everyone's inventory, and give all hunters a tracking compass.
4. When you're done, run `/manhunt stop` to end the game.

## Settings
Specific tracking settings for the compass can be toggled on or off to set the difficulty for the hunters.
Use the command `/manhunt settings <setting> <on|off>` to enable or disable a setting. _Example: /manhunt settings distance on_

Available settings:
- `distance`: Displays the distance from the hunter to the runner. _Default on._
- `ylevel`: Displays the height (y level) of the hunter.

**The distance and y level (if enabled) are displayed on the compass's display name**

### Config file (nerdy stuff)
You can also change the tracking settings in the config file located at `YourServer/plugins/Manhunt/config.yml`.
Default config:
```yml
track:
    distance: true
    ylevel: true
```

You can change the values from true/false to enable/disable the settings, respectively.
_Note: If settings are changed using the `/manhunt settings` command, it will automatically update the config file_

## Commands
- `manhunt track [<username>]"`: Display the player currently being tracked (username sets the player being tracked)
- `manhunt start`: Starts the game of manhunt
- `manhunt stop`: Ends the game of manhunt
- `manhunt settings <distance|ylevel> <on|off>`: Toggles different tracking features that compasses get.

_All these commands require the permission `manhunt.setup` to use._

## FAQ

Q: What if the runner and hunters are in a different dimension?
A: The hunter's compass will point to the portal that the runner went through. _If the hunters are in a dimension before the runner, the compass will point back to the portal they used._

_More will be added if needed_



**Please reach out to me if you have any issues through the GitHub Issues page or on discord at okay#2996, thank you :)**