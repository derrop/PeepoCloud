package net.peepocloud.lib.signlayout;


public class AnimatedSignLayout {
    private SignLayout[] animationSteps;
    private int animationStepCount;
    private transient int currentStep = 0;

    public AnimatedSignLayout(SignLayout[] animationSteps, int animationStepCount) {
        this.animationSteps = animationSteps;
        this.animationStepCount = animationStepCount;
    }

    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
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
