package li.cil.oc.common.item

import li.cil.oc.Constants
import li.cil.oc.Settings
import li.cil.oc.util.Color
import net.minecraft.client.renderer.block.model.ModelBakery
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn

class FloppyDisk(val parent: Delegator) extends traits.Delegate with CustomModel with traits.FileSystemLike {
  // Necessary for anonymous subclasses used for loot disks.
  override def unlocalizedName = "floppydisk"

  val kiloBytes = Settings.get.floppySize

  @OnlyIn(Dist.CLIENT)
  private def modelLocationFromDyeName(name: String) = {
    new ModelResourceLocation(Settings.resourceDomain + ":" + Constants.ItemName.Floppy + "_" + name, "inventory")
  }

  @OnlyIn(Dist.CLIENT)
  override def getModelLocation(stack: ItemStack): ModelResourceLocation = {
    val dyeIndex =
      if (stack.hasTagCompound && stack.getTagCompound.hasKey(Settings.namespace + "color"))
        stack.getTagCompound.getInteger(Settings.namespace + "color")
      else
        8
    modelLocationFromDyeName(Color.dyes(dyeIndex max 0 min 15))
  }

  @OnlyIn(Dist.CLIENT)
  override def registerModelLocations(): Unit = {
    for (dyeName <- Color.dyes) {
      val location = modelLocationFromDyeName(dyeName)
      ModelBakery.registerItemVariants(parent, new ResourceLocation(location.getResourceDomain + ":" + location.getResourcePath))
    }
  }

  override def doesSneakBypassUse(world: IBlockAccess, pos: BlockPos, player: EntityPlayer): Boolean = true
}
