package io.github.humorousfool.hmweapons.items.preset;

public enum PresetEffects
{
    CONSUMABLE(ConsumeEffect.class),
    EFFECT_LOCAL(EffectLocalEffect.class),
    ENTITY_TRAIL(EntityTrailEffect.class),
    PROJECTILE_LAUNCH(ProjectileLaunchEffect.class),
    SOUND_WORLD(SoundWorldEffect.class),
    SPAWN_RADIUS(SpawnRadiusEffect.class);

    PresetEffects(Class<? extends PresetEffect> clazz)
    {
        this.clazz = clazz;
    }

    public final Class<? extends PresetEffect> clazz;
}