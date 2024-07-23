# UHCRevamp

UHCRevamp is a comprehensive Ultra Hardcore (UHC) plugin for Minecraft servers, designed to enhance the survival experience by adding a competitive edge. This plugin introduces a variety of new gameplay mechanics, making survival more challenging and rewarding. Developed with a focus on performance and customization, UHCRevamp offers a wide range of features to create the ultimate UHC experience.

## Features

- **Customizable Game Settings**: Tailor every aspect of the game to your liking, from border sizes to loot drops.
- **Advanced Player Tracking**: Keep tabs on players with sophisticated tracking systems, ensuring fair play and competitive integrity.
- **Team Management**: Easily create and manage teams, with support for team-based objectives and scoring.
- **PlaceholderAPI Support**: Enhance your server's UI with custom placeholders, providing players with real-time game information.
- **Economy Integration**: Reward players for their performance in games with in-game currency, integrating seamlessly with Vault.
- **LuckPerms Support**: Utilize advanced permission management to control access to game features and commands.
- **ProtocolLib Integration**: Ensure compatibility and performance with ProtocolLib, enhancing the player experience.

## Installation

1. Download the UHCRevamp plugin from the [releases page](https://github.com/florixak/UHCRevamp/releases).
2. Place the downloaded `.jar` file into your server's `plugins` directory.
3. Restart your server to load the plugin.
4. Optionally, configure the plugin to your liking by editing the `config.yml` file in the `plugins/UHCRevamp` directory.

## Configuration

UHCRevamp is highly configurable, allowing server administrators to fine-tune the gameplay experience. Configuration files can be found in the `plugins/UHCRevamp` directory after the first server restart. For detailed information on each configuration option, please refer to the `config.yml` file comments.

## Commands

This plugin provides several commands to enhance the gameplay and administrative experience. Below is a list of available commands and their descriptions:

- `/uhcrun` (Aliases: `/urun`, `/uhcr`): Only for admins to setup the game.
- `/forcestart` (Aliases: `/start`): Force starts the game.
- `/workbench` (Aliases: `/wb`, `/craftingtable`): Opens a remote workbench for the player.
- `/anvil`: Opens a remote anvil interface.
- `/team` (Aliases: `/teams`): Opens the teams GUI for team management.
- `/kits` (Aliases: `/kit`): Opens the kits GUI for selecting kits.
- `/statistics` (Aliases: `/stats`): Opens the player statistics GUI.
- `/revive`: Revives a dead player.

Each command is designed with both players and administrators in mind, ensuring a smooth and enjoyable game experience.

## Dependencies

- **Minecraft Server (Spigot/Paper 1.8+)**: UHCRevamp is designed to work with Spigot or Paper servers.
- **PlaceholderAPI** (Optional): For custom placeholders support.
- **Vault** (Optional): For economy features.
- **LuckPerms** (Optional): For advanced permissions management.
- **ProtocolLib** (Optional): For enhanced features and performance.

## Support

For support, questions, or to contribute to the project, please visit the [GitHub repository](https://github.com/florixak/UHCRevamp).

## Author

FloriXak - [GitHub](https://github.com/florixak)

## License

UHCRevamp is licensed under the MIT License. See the `LICENSE` file for more details.
