package org.incendo.interfaces.example.kotlin

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.empty
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.incendo.interfaces.core.arguments.ArgumentKey
import org.incendo.interfaces.core.click.ClickHandler
import org.incendo.interfaces.core.transform.InterfaceProperty
import org.incendo.interfaces.kotlin.*
import org.incendo.interfaces.kotlin.getValue
import org.incendo.interfaces.kotlin.interfaceArgumentOf
import org.incendo.interfaces.kotlin.paper.asElement
import org.incendo.interfaces.kotlin.paper.buildChestInterface
import org.incendo.interfaces.kotlin.paper.buildPlayerInterface
import org.incendo.interfaces.kotlin.paper.open
import org.incendo.interfaces.kotlin.setValue
import org.incendo.interfaces.paper.PaperInterfaceListeners
import org.incendo.interfaces.paper.element.ItemStackElement
import org.incendo.interfaces.paper.pane.ChestPane
import org.incendo.interfaces.paper.type.ChestInterface
import org.incendo.interfaces.paper.type.PlayerInterface
import org.incendo.interfaces.paper.view.PlayerInventoryView

@Suppress("unused")
public class KotlinPlugin : JavaPlugin() {

    private companion object {
        private const val CHEST_ROWS: Int = 5
        private const val CHEST_COLUMNS: Int = 9

        private val CHEST_TITLE = text("Example Chest", NamedTextColor.GOLD)
        private val LEAVES =
            setOf(Material.EMERALD_BLOCK, Material.DIAMOND_BLOCK, Material.IRON_BLOCK)

        private val ARGUMENT_CONCRETE: ArgumentKey<Material> = argumentKeyOf("concrete")
    }

    private lateinit var exampleChest: ChestInterface
    private lateinit var examplePlayer: PlayerInterface

    private var _selectedOption: InterfaceProperty<SelectionOptions> =
        InterfaceProperty.of(SelectionOptions.ONE)

    override fun onEnable() {
        // Register the command.
        getCommand("interfaces")?.setExecutor(InterfaceCommandHandler())

        // Register event listeners.
        PaperInterfaceListeners.install(this)

        // Update the dependent value every time the server ticks.
        var selectedOption: SelectionOptions by _selectedOption

        // Build a chest interface.
        exampleChest =
            buildChestInterface {
                title = CHEST_TITLE
                rows = CHEST_ROWS

                clickHandler(
                    canceling {
                        it.viewer()
                            .player()
                            .sendMessage(
                                text("You clicked ", NamedTextColor.GRAY)
                                    .append(text(it.slot().toString(), NamedTextColor.GOLD)))
                    })

                withTransform(priority = 5) { view ->
                    println("rendering black concrete backing")

                    val displayElement: ItemStackElement<ChestPane> =
                        createItemStack(Material.BLACK_CONCRETE, text("")).asElement()

                    for (x in 3 until CHEST_COLUMNS - 1) {
                        for (y in 0 until CHEST_ROWS) {
                            view[x, y] = displayElement
                        }
                    }
                }

                withTransform { view ->
                    println("rendering options")
                    SelectionOptions.values().forEach { option ->
                        view[1, option.index] =
                            createItemStack(option.material, text(option.name)).asElement {
                                _selectedOption.set(option)
                            }
                    }
                }

                withTransform(_selectedOption) { view ->
                    println("rendering selected option")

                    // Extract an argument from the view
                    val concrete = view.arguments[ARGUMENT_CONCRETE]

                    val displayElement = createItemStack(concrete, text("")).asElement<ChestPane>()
                    selectedOption.art.forEach { (x, y) -> view[x, y] = displayElement }
                }

                withCloseHandler { event, _ -> event.player.sendMessage(text("bye")) }
            }

        examplePlayer =
            buildPlayerInterface {
                withTransform {
                    val num = (Bukkit.getCurrentTick() / 20) % 16

                    for (i in 0..3) {
                        val wool =
                            if (num and (1 shl i) != 0) {
                                Material.WHITE_WOOL
                            } else {
                                Material.BLACK_WOOL
                            }

                        it.armor[i] =
                            ItemStackElement.of(
                                createItemStack(wool, empty()), ClickHandler.cancel())
                    }
                }

                withTransform {
                    val tick = Bukkit.getCurrentTick()
                    val wools = Material.values().filter { material -> "WOOL" in material.name }

                    for (i in 0..8) {
                        val woolIndex = (i + tick) % wools.size

                        it.hotbar[i] =
                            ItemStackElement.of(
                                createItemStack(wools[woolIndex], empty()), ClickHandler.cancel())
                    }
                }

                withTransform {
                    for (x in 0..8) {
                        for (y in 0..2) {
                            val wool =
                                if (((Bukkit.getCurrentTick() / 2) % 9) + y == x) {
                                    Material.YELLOW_WOOL
                                } else {
                                    Material.RED_WOOL
                                }

                            it.main[x, y] =
                                ItemStackElement.of(
                                    createItemStack(wool, empty()), ClickHandler.cancel())
                        }
                    }
                }

                updates(true, 1)
            }
    }

    private fun createItemStack(material: Material, name: Component): ItemStack =
        ItemStack(material).also {
            it.itemMeta = it.itemMeta.also { meta -> meta.displayName(name) }
        }

    private inner class InterfaceCommandHandler : CommandExecutor {

        override fun onCommand(
            sender: CommandSender,
            command: Command,
            label: String,
            args: Array<out String>
        ): Boolean {
            if (sender !is Player) {
                sender.sendMessage(
                    text("Only players may execute this command", NamedTextColor.RED))
                return false
            }

            if (args.isEmpty()) {
                sender.sendMessage(
                    text("You must specify one of: chest, player, close", NamedTextColor.RED))
                return false
            }

            // Pass an argument to the interface.
            val arguments = interfaceArgumentOf(ARGUMENT_CONCRETE to Material.LIME_CONCRETE)

            when (args[0].toLowerCase()) {
                "chest" ->
                    sender.open(
                        exampleChest,
                        arguments,
                        text("Your Chest: ${sender.name}", NamedTextColor.GREEN))
                "player" -> sender.open(examplePlayer, arguments)
                "close" -> {
                    sender.closeInventory()
                    // Also close their player interface, if they have one open.
                    PlayerInventoryView.forPlayer(sender)?.close()
                }
                else ->
                    sender.sendMessage(
                        text("Unknown interface type '${args[0]}'", NamedTextColor.RED))
            }

            return true
        }
    }
}
