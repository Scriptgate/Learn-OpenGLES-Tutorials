package com.learnopengles.android.lesson2;

import com.learnopengles.android.common.Light;
import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.program.Program;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CubeRendererTest {

    @Mock private Cube cube;

    @Mock private Program program;
    @Mock private ModelViewProjectionMatrix mvpMatrix;
    @Mock private ModelMatrix modelMatrix;
    @Mock private ViewMatrix viewMatrix;
    @Mock private ProjectionMatrix projectionMatrix;
    @Mock private Light light;

    @Test
    public void drawCube() throws Exception {

        CubeRenderer.drawCube(cube, program, mvpMatrix, modelMatrix, viewMatrix, projectionMatrix, light);

        verify(cube).apply(modelMatrix);
        verify(cube).passPositionData(program);
        verify(cube).passColorData(program);
        verify(cube).passNormalData(program);
        verify(cube).passMVMatrix(program, mvpMatrix);
        verify(cube).passMVPMatrix(program, mvpMatrix);
        verify(cube).passLightTo(program, light);
        verify(cube).drawArrays();
        Mockito.verifyNoMoreInteractions(cube);
    }
}