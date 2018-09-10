package pl.piotrkalitka.TreeMngr;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import pl.piotrkalitka.TreeMngr.controller.MainController;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TreeMngrApplicationTest {

    @Autowired
    private MainController mainController;

    @Test
    public void contextLoads() {
        assertThat(mainController).isNotNull();
    }

}