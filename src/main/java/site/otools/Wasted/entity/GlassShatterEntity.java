package site.otools.Wasted.entity;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import site.otools.Wasted.item.ModItems;



public class GlassShatterEntity extends ThrowableItemProjectile {

    public GlassShatterEntity(EntityType<? extends GlassShatterEntity> type, Level level) {
        super(type, level);
    }

    public GlassShatterEntity(Level level, LivingEntity owner) {
        super(ModEntities.GLASS_SHATTER.get(), owner, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.GLASSHATTER.get();
    }


    @Override
    protected double getDefaultGravity() {
        return 0.01;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        // Sharp glass: 1.5 hearts of damage to the entity hit.
        result.getEntity().hurt(damageSources().thrown(this, getOwner()), 1.5F);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            ItemParticleOption particle = new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(this.getDefaultItem()));
            for (int i = 0; i < 8; i++) {
                this.level().addParticle(particle, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
            }
        } else {
            super.handleEntityEvent(id);
        }
    }
}
