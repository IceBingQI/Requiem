package ladysnake.dissolution.common.item;

import ladysnake.dissolution.Dissolution;
import ladysnake.dissolution.api.DissolutionPlayer;
import ladysnake.dissolution.api.remnant.RemnantHandler;
import ladysnake.dissolution.common.entity.PossessableEntityImpl;
import ladysnake.dissolution.common.impl.remnant.DefaultRemnantHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DebugItem extends Item {
    public DebugItem(Settings item$Settings_1) {
        super(item$Settings_1);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        RemnantHandler cap = ((DissolutionPlayer)player).getRemnantHandler();
        Dissolution.LOGGER.info("Player was {}", cap != null && cap.isSoul() ? "incorporeal" : "corporeal");
        if (!world.isClient) {
            if (cap == null) {
                Dissolution.LOGGER.info("Turned {} into a remnant", player);
                ((DissolutionPlayer)player).setRemnantHandler(cap = new DefaultRemnantHandler(player));
            }
            cap.setSoul(!cap.isSoul());
        }
        return new TypedActionResult<>(ActionResult.SUCCESS, player.getStackInHand(hand));
    }

    @Override
    public boolean beforeBlockBreak(BlockState blockState_1, World world_1, BlockPos blockPos_1, PlayerEntity playerEntity_1) {
        if (!world_1.isClient) {
            Entity entity = new PossessableEntityImpl(EntityType.ZOMBIE, world_1);
            entity.setPositionAndAngles(blockPos_1, 0, 0);
            world_1.spawnEntity(entity);
        }
        return super.beforeBlockBreak(blockState_1, world_1, blockPos_1, playerEntity_1);
    }
}
