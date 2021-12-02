package com.openanimationbackend.animation;

public class Animation {

    private String animation;

    public Animation(String animation) {
        this.animation = animation;
    }

    public String getAnimation() {
        return animation;
    }

    public void setAnimation(String animation) {
        this.animation = animation;
    }

    @Override
    public String toString() {
        return "Animation{" +
                "animation='" + animation + '\'' +
                '}';
    }
}
