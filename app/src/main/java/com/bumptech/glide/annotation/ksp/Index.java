package com.bumptech.glide.annotation.ksp;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
@interface Index
{
  public abstract String[] modules();
}