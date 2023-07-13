package io.github.wolfstorm.antimobgrief;

public abstract class Util {

    public static <T extends Enum<?>> String getEnumList(Class<T> enumType){
        return getEnumList(enumType, " | ");
    }

    public static <T extends Enum<?>> String getEnumList(Class<T> enumType, String delimiter){
        StringBuilder list = new StringBuilder();
        boolean put =false;
        for(Enum<?> e : enumType.getEnumConstants()){
            list.append(e.toString()).append(delimiter);
            put = true;
        }
        if(put) list = new StringBuilder(list.substring(0, list.length() - delimiter.length()));
        return list.toString();
    }
}
