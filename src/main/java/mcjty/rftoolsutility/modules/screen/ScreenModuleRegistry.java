package mcjty.rftoolsutility.modules.screen;

import mcjty.rftoolsbase.api.screens.IScreenModuleRegistry;
import mcjty.rftoolsbase.api.screens.data.IModuleDataFactory;
import mcjty.rftoolsutility.modules.screen.data.ModuleDataBoolean;
import mcjty.rftoolsutility.modules.screen.data.ModuleDataInteger;
import mcjty.rftoolsutility.modules.screen.data.ModuleDataString;
import mcjty.rftoolsutility.modules.screen.modules.ComputerScreenModule;
import mcjty.rftoolsutility.modules.screen.modules.ElevatorButtonScreenModule;
import mcjty.rftoolsutility.modules.screen.modules.ItemStackScreenModule;
import mcjty.rftoolsutility.modules.screen.modules.ScreenModuleHelper;

import java.util.*;

public class ScreenModuleRegistry implements IScreenModuleRegistry {

    private Map<String, IModuleDataFactory<?>> dataFactoryMap = new HashMap<>();
    private Map<String, Integer> idToIntMap = null;
    private Map<Integer, String> inttoIdMap = null;


    public void registerBuiltins() {
        dataFactoryMap.put(ModuleDataBoolean.ID, ModuleDataBoolean::new);
        dataFactoryMap.put(ModuleDataInteger.ID, ModuleDataInteger::new);
        dataFactoryMap.put(ModuleDataString.ID, ModuleDataString::new);
        dataFactoryMap.put(ScreenModuleHelper.ModuleDataContents.ID, ScreenModuleHelper.ModuleDataContents::new);
        dataFactoryMap.put(ItemStackScreenModule.ModuleDataStacks.ID, ItemStackScreenModule.ModuleDataStacks::new);
        dataFactoryMap.put(ElevatorButtonScreenModule.ModuleElevatorInfo.ID, ElevatorButtonScreenModule.ModuleElevatorInfo::new);
        dataFactoryMap.put(ComputerScreenModule.ModuleComputerInfo.ID, ComputerScreenModule.ModuleComputerInfo::new);
    }

    @Override
    public void registerModuleDataFactory(String id, IModuleDataFactory<?> dataFactory) {
        dataFactoryMap.put(id, dataFactory);
    }

    @Override
    public IModuleDataFactory<?> getModuleDataFactory(String id) {
        return dataFactoryMap.get(id);
    }

    public String getNormalId(int i) {
        createIdMap();
        return inttoIdMap.get(i);
    }

    public int getShortId(String id) {
        createIdMap();
        return idToIntMap.get(id);
    }

    private void createIdMap() {
        if (idToIntMap == null) {
            idToIntMap = new HashMap<>();
            inttoIdMap = new HashMap<>();
            List<String> strings = new ArrayList<>(dataFactoryMap.keySet());
            strings.sort(Comparator.<String>naturalOrder());
            int idx = 0;
            for (String s : strings) {
                idToIntMap.put(s, idx);
                inttoIdMap.put(idx, s);
                idx++;
            }
        }
    }
}
