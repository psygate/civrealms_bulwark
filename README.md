# Bulwark
=======

A simple minecraft server plugin for civ type servers using citadel.
Bulwark is intended to prevent the most common forms of griefing (lavaing, watering, droing) in a simple manner.

=======

## Requirements:

Bulwark makes extensive use of the ebeans layer provided by bukkit like servers. A database that can alter tables after they have been created is required. (In other words, NOT sqlite). Any other type of database should work (I tested it with a mysql database. Works fine there.)

=======

## What does it do?

Bulwark is configurable, but there are some things that can be prevented with it:
* **DRO or Citadel griefing**: Inside a Bulwark, only users on the citadel group can reinforce blocks.
* **Lava griefing**: Lava cannot be placed by non-citadel group member inside a bulwark. If lava is to move or drop into a bulwark area, it is stopped from doing so.
* **Water griefing**: The same as with lava.
* **Pearl-Teleporting**: Bulwarks can stop anyone from pearling through / in / out of a bulwark area. There is an option to allow group members to pass through a friendly bulwark.
=======
civrealms_bulwark
=================

Plugin for player driven foritication for the civrealms server

