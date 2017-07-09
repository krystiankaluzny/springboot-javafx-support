package de.felixroske.jfxsupport.view.plain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.roskenet.jfxsupport.test.GuiTest;
import javafx.scene.control.Button;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PlainView.class)
public class PlainViewTest extends GuiTest {

	@Autowired
	private PlainView buttonsView;

	@PostConstruct
	public void constructView() throws Exception {
		init(buttonsView);
	}

	@Test
	public void appStartsUp() throws Exception {
		assertThat(buttonsView, isA(AbstractFxmlView.class));
	}

	@Test
	public void bindWithFxmlTest() throws Exception {
		Button theButton = find("#theButton");

		assertNotNull(theButton);
	}
}
