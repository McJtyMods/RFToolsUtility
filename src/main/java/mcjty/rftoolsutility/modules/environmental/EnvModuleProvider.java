package mcjty.rftoolsutility.modules.environmental;

import mcjty.rftoolsutility.modules.environmental.modules.EnvironmentModule;

public interface EnvModuleProvider {

    Class<? extends EnvironmentModule> getServerEnvironmentModule();

    String getName();
}
