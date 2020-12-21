package ru.oshokin;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

@Component
public class TestBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory bf) throws BeansException {
        String[] beanDefinitionNames = bf.getBeanDefinitionNames();
        for (String s: beanDefinitionNames) {
            System.out.println("AYYYYAAA!!!! " + s);
        }
        BeanDefinition rollDefinition = bf.getBeanDefinition("colorCameraRoll");
        rollDefinition.setBeanClassName(BWCameraRoll.class.getName());
    }

}
