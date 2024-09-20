package dev.example.filmgallery.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.compose.AsyncImage
import dev.example.filmgallery.R
import dev.example.filmgallery.domain.state.MainScreenState
import dev.example.network.domain.model.Film
import org.koin.android.ext.android.inject

class MainFragment : Fragment() {
    private val viewModel: AppViewModel by inject<AppViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainFragment", "onCreate called")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("MainFragment", "MainFragment created")

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                MainScreen(
                    viewModel = viewModel,
                    onFilmSelected = { film ->
                        findNavController().navigate(
                            R.id.action_mainFragment_to_detailFragment,
                            Bundle().apply {
                                putSerializable("film", film)
                            }
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: AppViewModel,
    onFilmSelected: (Film) -> Unit
) {
    Log.d("MainScreen", "Composable function recomposed")
    val mainScreenState by viewModel.mainScreenState.collectAsState()
    val selectedGenre by viewModel.selectedGenre.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val isFirstLaunch = rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(mainScreenState) {
        if (mainScreenState is MainScreenState.Error && !isFirstLaunch.value) {
            snackbarHostState.showSnackbar(
                message = "Ошибка подключения к сети",
                actionLabel = "Повторить"
            ).let { result ->
                if (result == SnackbarResult.ActionPerformed) {
                    viewModel.retryLoading()
                }
            }
        }
        isFirstLaunch.value = false
        Log.d("MainScreen", "Is first launch: ${isFirstLaunch.value}")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Фильмы",
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        fontWeight = FontWeight(500),
                        fontSize = 18.sp,
                        lineHeight = 22.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                colors = TopAppBarDefaults
                    .topAppBarColors(containerColor = Color(0xFF0E3165)),
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = {
                    Snackbar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .padding(bottom = 5.dp),
                        containerColor = Color.Black,
                        contentColor = Color.White,
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Ошибка подключения к сети")
                            TextButton(
                                onClick = {
                                    viewModel.retryLoading()
                                }
                            ) {
                                Text(
                                    text = "Повторить",
                                    color = Color(0xFFFFC967)
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(top = 10.dp),
        ) {
            when (mainScreenState) {
                is MainScreenState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFFFFC967),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                is MainScreenState.Success -> {
                    GenreList(
                        genres = viewModel.getGenres(),
                        selectedGenre = selectedGenre,
                        onGenreSelected = { genre ->
                            viewModel.toggleGenreFilter(genre)
                        }
                    )
                    FilmList(
                        films = (mainScreenState as MainScreenState.Success).films,
                        onFilmSelected = onFilmSelected
                    )
                }
                is MainScreenState.Error -> {

                }
            }
        }
    }
}

@Composable
fun GenreList(
    genres: List<String>,
    selectedGenre: String?,
    onGenreSelected: (String?) -> Unit
) {
    Text(
        text = "Жанры",
        fontWeight = FontWeight(700),
        fontSize = 20.sp,
        lineHeight = 22.sp,
        modifier = Modifier
            .padding(horizontal = 16.dp)
    )
    Spacer(modifier = Modifier.height(5.dp))
    Column(
        modifier = Modifier
    ){
        genres.forEach { genre ->
            val isSelected = selectedGenre?.lowercase() == genre.lowercase()
            val backgroundColor = if (isSelected) Color(0xFFFFC967) else Color.Transparent
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
            ){
                Text(
                    text = genre,
                    fontWeight = FontWeight(400),
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier
                        .clickable(
                            onClick = { onGenreSelected(if (isSelected) null else genre.lowercase()) }
                        )
                        .padding(horizontal = 16.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun FilmList(films: List<Film>, onFilmSelected: (Film) -> Unit) {
    val sortedFilms = films.sortedBy { it.localized_name }

    Column {
        Text(
            text = "Фильмы",
            fontWeight = FontWeight(700),
            fontSize = 20.sp,
            lineHeight = 22.sp,
            modifier = Modifier.padding(16.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(horizontal = 7.dp)
        ) {
            items(sortedFilms) { film ->
                FilmItem(film = film, onClick = { onFilmSelected(film) })
            }
        }
    }
}

@Composable
fun FilmItem(film: Film, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(bottom = 15.dp),
        horizontalAlignment = Alignment.Start,
    ){
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
        ) {
            AsyncImage(
                model = film.image_url,
                contentDescription = film.localized_name,
                error = painterResource(id = R.drawable.placeholder),
                modifier = Modifier
                    .height(250.dp)
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = film.localized_name,
            fontWeight = FontWeight(700),
            fontSize = 16.sp,
            lineHeight = 20.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .width(135.dp)
                .padding(horizontal = 8.dp)
        )
    }
}