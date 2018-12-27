package net.peepocloud.lib.serverselector.signselector;


import com.google.common.base.Preconditions;

public class AnimatedSignLayout {
    private SignLayout[] animationSteps;
    private int animationStepCount;
    private transient int currentStep = 0;

    public AnimatedSignLayout(SignLayout[] animationSteps, int animationStepCount) {
        Preconditions.checkArgument(animationStepCount > 0 && animationSteps.length > 0, "Animation must have at least one step");

        this.animationSteps = animationSteps;
        this.animationStepCount = animationStepCount;
    }

    public void nextStep() {
        this.currentStep++;
        if(this.currentStep >= this.animationStepCount - 1)
            this.currentStep = 0;
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public SignLayout getCurrentLayout() {
        return this.animationSteps[this.getCurrentStep()];
    }

    public int getAnimationStepCount() {
        return animationStepCount;
    }

    public SignLayout[] getAnimationSteps() {
        return animationSteps;
    }
}
