# UHC Revamp

UHC Revamp is a UHC minigame for Minecraft servers, designed to enhance the survival
experience by adding a competitive edge. This plugin introduces a variety of new gameplay mechanics, making survival
more challenging and rewarding. Developed with a focus on performance and customization, UHC Revamp offers a wide range
of features to create the great UHC experience.

## Features

- **Customizable Game Settings**: Tailor every aspect of the game to your liking, from border sizes to loot drops.
- **Advanced Player Tracking**: Keep tabs on players with sophisticated tracking systems, ensuring fair play and
  competitive integrity.
- **Team Management**: Easily create and manage teams, with support for team-based objectives and scoring.
- **PlaceholderAPI Support**: Enhance your server's UI with custom placeholders, providing players with real-time game
  information.
- **Economy Integration**: Reward players for their performance in games with in-game currency, integrating seamlessly
  with Vault.
- **LuckPerms Support**: Utilize advanced permission management to control access to game features and commands.
- **ProtocolLib Integration**: Ensure compatibility and performance with ProtocolLib, enhancing the player experience.
- **Custom Kits, Perks, Drops, and Recipes**: Customize kits, loot drops, and crafting recipes for a unique gameplay
  experience.
- **Custom Ore Generation**: You do not like standard amount of ore generating? Customize it to suit your preferences.

## Prerequisites

- Minecraft server version 1.8 or higher
- Java 8 or higher
- PlaceholderAPI plugin (for placeholder support)
- (Optional) Vault plugin (for economy integration)
- (Optional) LuckPerms plugin (for permission management)
- (Optional) ProtocolLib plugin (for enhanced compatibility)

## Installation

1. Download the UHCRevamp plugin.
2. Place the downloaded `.jar` file into your server's `plugins` directory.
3. Restart your server to load the plugin.
4. Optionally, configure the plugin to your liking by editing the `config.yml` file in the `plugins/UHCRevamp`
   directory. Or configure messages and other settings as needed.
5. Start your server and enjoy the new UHC experience!

## Configuration

UHCRevamp provides several configuration files to customize various aspects of the game. Below is a list of available
configuration types and their purposes:

- **CONFIG.YML**: General settings for the plugin, including game rules, border sizes, and other global configurations.
- **MESSAGES.YML**: Customizable messages that the plugin sends to players, allowing for localization and
  personalization.
- **SCOREBOARD.YML**: Configuration for the in-game scoreboard, including what information is displayed and how it is
  formatted.
- **PLAYER\_DATA.YML**: Stores data related to individual players, such as statistics and achievements.
- **TEAMS.YML**: Configuration for team settings, including team names, colors, and other team-related options.
- **KITS.YML**: Customizable kits that players can select, including items and equipment provided in each kit.
- **PERKS.YML**: Configuration for player perks, allowing customization of special abilities or bonuses players can
  receive.
- **CUSTOM\_DROPS.YML**: Settings for custom item drops, defining what items can drop from various events or actions.
- **CUSTOM\_RECIPES.YML**: Custom crafting recipes that can be added to the game, allowing for unique items and crafting
  mechanics.
- **ORE\_GENERATION.YML**: Configuration for ore generation, including custom ore types and generation rules.

## Commands and Permissions

This plugin provides several commands to enhance the gameplay and administrative experience. Below is a list of
available commands, their descriptions, and the required permissions:

- `/uhc` (Aliases: `/uhcr`, `/uhcrevamp`)
    - Description: Command to display help information.
    - Permission: `uhcrevamp.setup`, `uhcrevamp.vip`
- `/forcestart` (Aliases: `/start`)
    - Description: Immediately starts the UHC game, bypassing minimal needed players.
    - Permission: `uhcrevamp.forcestart`
- `/workbench` (Aliases: `/wb`, `/craftingtable`)
    - Description: Opens a virtual crafting table for the player, allowing remote crafting.
    - Permission: `uhcrevamp.workbench`
- `/anvil`
    - Description: Opens a virtual anvil interface for the player, enabling remote item repair and renaming.
    - Permission: `uhcrevamp.anvil`
- `/team` (Aliases: `/teams`)
    - Description: Opens the team management GUI, allowing players to create, join, or manage teams.
- `/kits` (Aliases: `/kit`)
    - Description: Opens the kits selection GUI, where players can choose from available kits.
- `/recipes` (Aliases: `/crafts`, `/customrecipes`)
    - Description: Opens the custom recipes GUI, displaying all available custom crafting recipes.
- `/statistics` (Aliases: `/stats`)
    - Description: Opens the player statistics GUI, showing detailed player stats and achievements.
- `/revive`
    - Description: Revives a dead player, bringing them back into the game.
    - Permission: `uhcrevamp.revive`

### Non-Command Permissions

- `uhcrevamp.color-chat`
    - Description: Allows the player to use color codes in chat.
- `uhcrevamp.reserved-slot`
    - Description: Allows the player to join the server when it is full.
- `uhcrevamp.spectate`
    - Description: Allows the player to join and spectate playing games.

## Usage

After installing and configuring the plugin, you can start a UHC game by using the `/uhc` command. For a full list of
commands and their usage, refer to the Commands and Permissions section.

## License

This project is licensed under the MIT License. See the `LICENSE` file for more details.

## Support

If you encounter any issues or have any questions, please open an issue on the GitHub repository or contact the
developers through the provided support channels.

## Legal

Please do not steal this plugin. This plugin is the intellectual property of its developer(s). Unauthorized
distribution, modification, or use of this plugin without permission is strictly prohibited.
