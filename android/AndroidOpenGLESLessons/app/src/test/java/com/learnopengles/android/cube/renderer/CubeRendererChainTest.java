package com.learnopengles.android.cube.renderer;

import com.learnopengles.android.common.Light;
import com.learnopengles.android.component.ModelMatrix;
import com.learnopengles.android.component.ModelViewProjectionMatrix;
import com.learnopengles.android.component.ProjectionMatrix;
import com.learnopengles.android.component.ViewMatrix;
import com.learnopengles.android.cube.Cube;
import com.learnopengles.android.cube.renderer.mvp.ModelViewCubeRenderer;
import com.learnopengles.android.program.Program;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CubeRendererChainTest {

    @Mock
    private Cube cube;

    @Mock
    private Program program;
    @Mock
    private ModelViewProjectionMatrix mvpMatrix;
    @Mock
    private ModelMatrix modelMatrix;
    @Mock
    private ViewMatrix viewMatrix;
    @Mock
    private ProjectionMatrix projectionMatrix;
    @Mock
    private Light light;

    private CubeRendererChain cubeRendererChain;

    @Before
    public void setUp() throws Exception {
        cubeRendererChain = new CubeRendererChain(
                asList(
                        new ModelMatrixCubeRenderer(modelMatrix),

                        new PositionCubeRenderer(program),
                        new ColorCubeRenderer(program),
                        new NormalCubeRenderer(program),

                        new ModelViewCubeRenderer(mvpMatrix, modelMatrix, viewMatrix, projectionMatrix, program),

                        new LightCubeRenderer(light, program)
                )
        );
    }

    @Test
    public void drawCube() throws Exception {
        cubeRendererChain.drawCube(cube);

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