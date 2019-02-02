package ladysnake.dissolution.common.impl.possession;

import ladysnake.dissolution.api.v1.possession.Possessable;
import ladysnake.dissolution.api.v1.possession.conversion.PossessableSubstitutionHandler;
import ladysnake.dissolution.api.v1.possession.conversion.PossessionConversionRegistry;
import ladysnake.dissolution.common.tag.DissolutionEntityTags;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class SimplePossessionConversionRegistry implements PossessionConversionRegistry {
    protected final Map<EntityType<? extends MobEntity>, PossessableSubstitutionHandler> converters = new HashMap<>();

    public boolean isAllowed(EntityType<?> entityType) {
        return !DissolutionEntityTags.POSSESSION_BLACKLIST.contains(entityType);
    }

    @Override
    public <T extends MobEntity> void registerPossessedConverter(EntityType<T> baseEntityType, PossessableSubstitutionHandler<T> possessedEntityType) {
        this.converters.put(baseEntityType, possessedEntityType);
    }

    @Override
    public boolean canBePossessed(EntityType<?> entityType) {
        //it's fine, if the entity type is not that of a mob, it just won't be in the map
        //noinspection SuspiciousMethodCalls
        return isAllowed(entityType) && this.converters.containsKey(entityType);
    }

    @Nullable
    @Override
    public <T extends MobEntity> Possessable convert(T entity, PlayerEntity possessor) {
        if (!this.canBePossessed(entity)) {
            return null;
        }
        PossessableSubstitutionHandler<T> converter = this.getConverterFor(entity.getType(), entity.getClass());
        if (converter != null) {
            return converter.apply(entity, possessor);
        }
        return null;
    }

    /**
     * Gets the appropriate converter for entities of the given type.
     * <em>Note: the returned substitution handler is unsafely casted and should only be used on the same entity type</em>
     */
    @Nullable
    @SuppressWarnings({"unchecked", "SuspiciousMethodCalls"})
    protected <T extends MobEntity> PossessableSubstitutionHandler<T> getConverterFor(EntityType<?> entityType, Class<?> entityClass) {
        return (PossessableSubstitutionHandler<T>) this.converters.get(entityType);
    }

    @Override
    public boolean isEntityRegistered(EntityType<? extends MobEntity> entityType) {
        return false;
    }
}
