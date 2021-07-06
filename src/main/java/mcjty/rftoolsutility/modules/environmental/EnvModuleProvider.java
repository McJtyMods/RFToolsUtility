package mcjty.rftoolsutility.modules.environmental;

import mcjty.rftoolsutility.modules.environmental.modules.EnvironmentModule;

import java.util.function.Supplier;

public interface EnvModuleProvider {

    Supplier<? extends EnvironmentModule> getServerEnvironmentModule();

    String getName();
}
