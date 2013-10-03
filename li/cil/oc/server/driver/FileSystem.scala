package li.cil.oc.server.driver

import li.cil.oc
import li.cil.oc.api.driver.{Item, Slot}
import li.cil.oc.{Config, Items}
import net.minecraft.item.ItemStack

object FileSystem extends Item {
  override def api = Option(getClass.getResourceAsStream(Config.driverPath + "fs.lua"))

  override def worksWith(item: ItemStack) = WorksWith(Items.hdd)(item)

  override def slot(item: ItemStack) = Slot.HDD

  override def node(item: ItemStack) = {
    // We have a bit of a chicken-egg problem here, because we want to use the
    // node's address as the folder name... so we generate the address here,
    // if necessary. No one will know, right? Right!?
    val tag = nbt(item)
    val address =
      if (tag.hasKey("address"))
        tag.getString("address")
      else
        java.util.UUID.randomUUID().toString
    oc.api.FileSystem.fromSaveDir(address).flatMap(oc.api.FileSystem.asNode) match {
      case None => None
      case Some(node) =>
        node.address = Some(address)
        node.load(tag)
        Some(node)
    }
  }
}