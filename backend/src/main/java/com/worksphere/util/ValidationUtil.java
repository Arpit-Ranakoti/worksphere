package com.worksphere.util; public final class ValidationUtil {private ValidationUtil(){} public static void require(boolean ok,String message){if(!ok)throw new IllegalArgumentException(message);} }
