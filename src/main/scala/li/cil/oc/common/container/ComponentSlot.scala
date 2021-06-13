package li.cil.oc.common.container

import li.cil.oc.common
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn

import scala.collection.convert.WrapAsScala._

abstract class ComponentSlot(inventory: IInventory, index: Int, x: Int, y: Int) extends Slot(inventory, index, x, y) {
  def container: Player

  def slot: String

  def tier: Int

  def tierIcon: ResourceLocation

  var changeListener: Option[Slot => Unit] = None

  // ----------------------------------------------------------------------- //

  def hasBackground = backgroundLocation != null

  @OnlyIn(Dist.CLIENT)
  override def isEnabled = slot != common.Slot.None && tier != common.Tier.None && super.isEnabled

  override def isItemValid(stack: ItemStack) = inventory.isItemValidForSlot(getSlotIndex, stack)

  override def onTake(player: EntityPlayer, stack: ItemStack) = {
    for (slot <- container.inventorySlots) slot match {
      case dynamic: ComponentSlot => dynamic.clearIfInvalid(player)
      case _ =>
    }
    super.onTake(player, stack)
  }

  override def putStack(stack: ItemStack): Unit = {
    super.putStack(stack)
    inventory match {
      case playerAware: common.tileentity.traits.PlayerInputAware =>
        playerAware.onSetInventorySlotContents(container.playerInventory.player, getSlotIndex, stack)
      case _ =>
    }
  }

  override def onSlotChanged() {
    super.onSlotChanged()
    for (slot <- container.inventorySlots) slot match {
      case dynamic: ComponentSlot => dynamic.clearIfInvalid(container.playerInventory.player)
      case _ =>
    }
    changeListener.foreach(_(this))
  }

  protected def clearIfInvalid(player: EntityPlayer) {}
}
