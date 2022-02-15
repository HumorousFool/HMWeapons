package io.github.humorousfool.hmweapons.items.preset;

public enum PresetEffects
{
    BLOCK_BREAK(BlockBreakEffect.class),
    CONSUME(ConsumeEffect.class),
    POTION_LOCAL(PotionLocalEffect.class),
    ENTITY_TRAIL(EntityTrailEffect.class),
    EVENT_TYPE(EventTypeEffect.class),
    GIVE_ITEM(GiveItemEffect.class),
    HELD_SLOT(HeldSlotEffect.class),
    LOOP(LoopEffect.class),
    PROJECTILE_LAUNCH(ProjectileLaunchEffect.class),
    RANDOM(RandomEffect.class),
    SEND_MESSAGE(SendMessageEffect.class),
    SOUND_WORLD(SoundWorldEffect.class),
    SPAWN_RADIUS(SpawnRadiusEffect.class);

    PresetEffects(Class<? extends PresetEffect> clazz)
    {
        this.clazz = clazz;
    }

    public final Class<? extends PresetEffect> clazz;
}