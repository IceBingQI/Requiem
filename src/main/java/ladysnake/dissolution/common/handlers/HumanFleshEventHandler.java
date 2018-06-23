package ladysnake.dissolution.common.handlers;

import ladysnake.dissolution.common.OreDictHelper;
import ladysnake.dissolution.common.Reference;
import ladysnake.dissolution.common.capabilities.CapabilityIncorporealHandler;
import ladysnake.dissolution.common.registries.SoulStates;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class HumanFleshEventHandler {
    /**
     * Handles undead purification through human flesh consumption. Don't ask.
     */
    @SubscribeEvent
    public static void onLivingEntityUseItem(LivingEntityUseItemEvent.Finish event) {
        if (OreDictHelper.doesItemMatch(event.getItem(), OreDictHelper.HUMAN_FLESH_RAW) || event.getItem().getItem() == Items.GOLDEN_APPLE) {
            CapabilityIncorporealHandler.getHandler(event.getEntityLiving()).ifPresent(handler -> {
                EntityLivingBase possessed = handler.getPossessed();
                if (possessed != null) {
                    if (event.getItem().getItem() == Items.GOLDEN_APPLE) {
                        if (event.getEntityLiving().isPotionActive(MobEffects.WEAKNESS)) {
                            handler.setCorporealityStatus(SoulStates.BODY);
                            possessed.world.removeEntity(possessed);
                        }
                    } else {
                        if (possessed.world.rand.nextFloat() < ((possessed.getHealth() / possessed.getMaxHealth()) * 0.8)) {
                            handler.setCorporealityStatus(SoulStates.BODY);
                            possessed.world.removeEntity(possessed);
                        } else {
                            possessed.heal(4f);
                        }
                    }
                }
            });
        }
    }
}
