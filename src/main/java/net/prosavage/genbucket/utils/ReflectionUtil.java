package net.prosavage.genbucket.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionUtil {

   public static Object getField(Object object, String fieldName) {
      try {
         Field field = object.getClass().getDeclaredField(fieldName);
         field.setAccessible(true);
         return field.get(object);
      } catch (Exception e) {
         e.printStackTrace();
      }
      return null;
   }

   public static void setField(Object object, Object value, String fieldName) {
      try {
         Field field = object.getClass().getDeclaredField(fieldName);
         field.setAccessible(true);
         field.set(object, value);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public static Object invokeMethod(Object object, String methodName, ArgTypes argTypes, Object... args) {
      try {
         Method method = object.getClass().getDeclaredMethod(methodName, argTypes.classes);
         method.setAccessible(true);
         return method.invoke(object, args);
      } catch (Exception e) {
         e.printStackTrace();
      }
      return null;
   }

   public static class ArgTypes {
      Class[] classes;

      public ArgTypes(Class... classes) {
         this.classes = classes;
      }
   }
}