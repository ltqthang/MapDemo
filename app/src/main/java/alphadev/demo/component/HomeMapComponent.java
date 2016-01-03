package alphadev.demo.component;

import alphadev.demo.fragment.HomeMapFragment;
import alphadev.demo.module.HomeMapModule;
import dagger.Component;

@Component(modules = HomeMapModule.class)
public interface HomeMapComponent {
    void inject(HomeMapFragment fragment);
}
