package net.millioners.worldswithores.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.millioners.worldswithores.WorldsWithOresMod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, WorldsWithOresMod.MOD_ID);

    public static final RegistryObject<SoundEvent> REACTOR_START = register("reactor_start");
    public static final RegistryObject<SoundEvent> REACTOR_HUM = register("reactor_hum");
    public static final RegistryObject<SoundEvent> REACTOR_COOLANT = register("reactor_coolant");
    public static final RegistryObject<SoundEvent> REACTOR_OVERHEAT = register("reactor_overheat");

    private ModSounds() {}

    private static RegistryObject<SoundEvent> register(String name) {
        ResourceLocation id = new ResourceLocation(WorldsWithOresMod.MOD_ID, name);
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }
}
