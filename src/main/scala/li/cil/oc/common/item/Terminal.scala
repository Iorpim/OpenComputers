package li.cil.oc.common.item

import java.util

import li.cil.oc.Constants
import li.cil.oc.OpenComputers
import li.cil.oc.Settings
import li.cil.oc.common.GuiType
import net.minecraft.client.renderer.block.model.ModelBakery
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn

class Terminal(val parent: Delegator) extends traits.Delegate with CustomModel {
  override def maxStackSize = 1

  def hasServer(stack: ItemStack) = stack.hasTagCompound && stack.getTagCompound.hasKey(Settings.namespace + "server")

  @OnlyIn(Dist.CLIENT)
  override def tooltipLines(stack: ItemStack, world: World, tooltip: util.List[String], flag: ITooltipFlag) {
    super.tooltipLines(stack, world, tooltip, flag)
    if (hasServer(stack)) {
      val server = stack.getTagCompound.getString(Settings.namespace + "server")
      tooltip.add("§8" + server.substring(0, 13) + "...§7")
    }
  }

  @OnlyIn(Dist.CLIENT)
  private def modelLocationFromState(running: Boolean) = {
    new ModelResourceLocation(Settings.resourceDomain + ":" + Constants.ItemName.Terminal + (if (running) "_on" else "_off"), "inventory")
  }

  @OnlyIn(Dist.CLIENT)
  override def getModelLocation(stack: ItemStack): ModelResourceLocation = {
    modelLocationFromState(hasServer(stack))
  }

  @OnlyIn(Dist.CLIENT)
  override def registerModelLocations(): Unit = {
    for (state <- Seq(true, false)) {
      val location = modelLocationFromState(state)
      ModelBakery.registerItemVariants(parent, new ResourceLocation(location.getResourceDomain + ":" + location.getResourcePath))
    }
  }

  override def onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer): ActionResult[ItemStack] = {
    if (!player.isSneaking && stack.hasTagCompound) {
      val key = stack.getTagCompound.getString(Settings.namespace + "key")
      val server = stack.getTagCompound.getString(Settings.namespace + "server")
      if (key != null && !key.isEmpty && server != null && !server.isEmpty) {
        if (world.isRemote) {
          player.openGui(OpenComputers, GuiType.Terminal.id, world, 0, 0, 0)
        }
        player.swingArm(EnumHand.MAIN_HAND)
      }
    }
    super.onItemRightClick(stack, world, player)
  }
}
