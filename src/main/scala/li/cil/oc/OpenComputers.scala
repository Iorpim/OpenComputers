package li.cil.oc

import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.Mod.EventHandler
import cpw.mods.fml.common.SidedProxy
import cpw.mods.fml.common.event.{FMLFingerprintViolationEvent, FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import cpw.mods.fml.common.network.NetworkMod
import cpw.mods.fml.common.network.NetworkMod._
import java.util.logging.Logger
import li.cil.oc.client.{PacketHandler => ClientPacketHandler}
import li.cil.oc.common.Proxy
import li.cil.oc.server.{PacketHandler => ServerPacketHandler}

@Mod(modid = "OpenComputers", modLanguage = "scala",
  certificateFingerprint = "@FINGERPRINT@", useMetadata = true)
@NetworkMod(clientSideRequired = true, serverSideRequired = false,
  clientPacketHandlerSpec = new SidedPacketHandler(
    channels = Array("OpenComp"), packetHandler = classOf[ClientPacketHandler]),
  serverPacketHandlerSpec = new SidedPacketHandler(
    channels = Array("OpenComp"), packetHandler = classOf[ServerPacketHandler]))
object OpenComputers {
  val log = Logger.getLogger("OpenComputers")

  @SidedProxy(clientSide = "li.cil.oc.client.Proxy", serverSide = "li.cil.oc.server.Proxy")
  var proxy: Proxy = null

  var tampered: Option[FMLFingerprintViolationEvent] = None

//  @EventHandler
//  def invalidFingerprint(e: FMLFingerprintViolationEvent) = tampered = Some(e)

  @EventHandler
  def preInit(e: FMLPreInitializationEvent) = proxy.preInit(e)

  @EventHandler
  def init(e: FMLInitializationEvent) = proxy.init(e)

  @EventHandler
  def postInit(e: FMLPostInitializationEvent) = proxy.postInit(e)
}