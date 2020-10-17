![ChatCat Logo](https://imgur.com/9yyAb4G.png)

# ChatCat

ChatCat is a chat plugin designed to be easily configurable and compatible with [Vault](https://www.spigotmc.org/resources/vault.34315/) and [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/).
It provides functionality like permanent and temporary mutes with an intelligent caching system and ISO-compatible timespan notation, along with simple yet powerful modular chat prefixing. **ChatCat supports Spigot/Paper 1.16.3 on Java 11.**

ChatCat's messages are completely configurable - there does not exist a plugin message you can't customise in the config.yml file.

### Installation
1. Download ChatCat from [SpigotMC](https://example.com/) or [build it yourself](https://example.com/)
2. Move/upload the downloaded .jar to your server's `plugins` folder
3. Restart your server


### Commands
```
/mute        - Mutes a player. Permission: chatcat.mutes.mute
  [-h]       - Doesn't broadcast the mute. Short for 'hidden'
  [-s]       - Makes the mute a soft mute. A soft-muted player sees their own messages, but nobody else can see them. Short for 'soft'
  <player>   - The player to be muted.
  <duration> - The duration of the mute. Examples for valid durations are 'forever', 'eternity', 'P3dT10h' (see https://en.wikipedia.org/wiki/ISO_8601)
  [reason]   - The reason for the mute.

/unmute    - Unmutes a muted player. Permission: chatcat.mutes.unmute
  [-h]     - Doesn't broadcast the unmute. Short for 'hidden'
  <player> - The player to be unmuted.


/chatcat [reload] - Shows help message or reloads ChatCat. Permission: chatcat.admin

/nick        - Sets your or another player's nickname.
  [player]   - The player. Defaults to you if not provided (required for console).
  <nick>     - The nick. Surround with double-quotes (") to use spaces.
```

### Permissions
- chatcat.admin        - Allows usage of /chatcat
- chatcat.mutes.mute   - Allows muting of players
- chatcat.mutes.unmute - Allows unmuting of players.
- chatcat.format       - Allows formatting of messages (&c and &k, for example)
- chatcat.nick         - Allows a player to /nick themselves
- chatcat.nick.other   - Allows a player to /nick other players
  

### Configuration

ChatCat ships with a configuration file located in `plugins/ChatCat/config.yml`. This file allows customisation of the chat format, logging level, and messages of ChatCat.`

Here is an example config.yml file with comments.
```yml
# Whether to use the message prefix or not. Defaults to true
use_message_prefix: true

# The message prefix, with some default placeholders as well as any PlaceholderAPI placeholders.
# Default placeholders:
#   $name - The player's name
#   $nick - The player's nickname / display name
#   $prefix - The player's Vault prefix, if Vault is installed
#   $suffix - The player's Vault suffix, if Vault is installed
#
# Here, %eglow_glowcolor% is a PlaceholderAPI placeholder registered by eGlow.
message-prefix: '$prefix%eglow_glowcolor%$nick$suffix» '
messages:

  # This part is fairly self-explanatory, so I won't go over these.
  generic:
    no-permission: '&cYou do not have permission to do that!'
    invalid-arguments: '&cInvalid arguments!'
    never-played-before: '&cThat player has never played before!'
    must-specify-player: '&cNon-player executors must specify a player!'
  mutes:
    # Here, you can use %1$s to represent the mute time, and %2$s to represent the reason. (Or use %s, the order decides which is which.)
    muted: '&fPlayer &a%s &fwas muted for &a%s &fwith reason ''&a%s&f'''
    # Here, you can use %s or %1$s to represent the reason.
    muted-no-reason: '&fPlayer &a%s &fwas muted for &a%s&f.'
    # Here, %s represents the unmuted player.
    unmuted: '&fPlayer &a%s &fwas unmuted.'
    not-muted: '&cThat player is not muted!'
    # Here, you can use %1$s to represent the remaining mute time, and %2$s to represent the reason. (Or use %s, the order decides which is which.)
    mute-notification: '&fYou are &cmuted&f for &c%s&f! Reason: &c%s'
    # Here, you can use %s to represent the remaining mute time.
    mute-notification-no-reason: '&fYou are &cmuted&f for &c%s&f!'
  nick:
    # Here, you can use %1$s to represent the player ('Your' if the player is the command sender, otherwise playername + 's), and %2$s to represent the new nick.
    nick-set: '&a%s nick was set to &a"&f%s&a".'

# This can be changed to represent a valid java.util.logging.Level log level to log more or less information.
logLevelValue: INFO
```
