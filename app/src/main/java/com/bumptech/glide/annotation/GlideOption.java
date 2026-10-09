package com.bumptech.glide.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface GlideOption
{
  public static final int OVERRIDE_NONE = 0;
  public static final int OVERRIDE_EXTEND = 1;
  public static final int OVERRIDE_REPLACE = 2;

  public abstract int override();

  public abstract String staticMethodName();

  public abstract boolean memoizeStaticMethod();

  public abstract boolean skipStaticMethod();
}