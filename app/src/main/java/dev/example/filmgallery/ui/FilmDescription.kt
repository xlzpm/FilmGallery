package dev.example.filmgallery.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierInfo
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
import dev.example.network.domain.model.Film
import java.math.RoundingMode

class FilmDescriptionFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val film = arguments?.getSerializable("film") as? Film

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                film?.let {
                    FilDescription(
                        film = it,
                        onBack = {
                            findNavController().popBackStack()
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilDescription(
    film: Film,
    onBack: () -> Unit
){
    val scrollState = rememberScrollState()
    val filmRatingAround = film.rating
        .toBigDecimal().setScale(1, RoundingMode.HALF_UP).toDouble()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ){
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "button back",
                            tint = Color.White,
                            modifier = Modifier
                                .clickable(onClick = onBack)
                        )
                        Text(
                            text = film.name,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight(500),
                            fontSize = 18.sp,
                            lineHeight = 22.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                },
                colors = TopAppBarDefaults
                    .topAppBarColors(containerColor = Color(0xFF0E3165)),
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            AsyncImage(
                model = film.image_url,
                contentDescription = film.localized_name,
                error = painterResource(id = R.drawable.placeholder),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = film.localized_name,
                fontWeight = FontWeight(700),
                fontSize = 26.sp,
                lineHeight = 32.sp,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "${film.genres[0]}, ${film.year} год",
                color = Color(0xFF4B4B4B),
                fontWeight = FontWeight(400),
                fontSize = 16.sp,
                lineHeight = 20.sp,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "$filmRatingAround",
                    color = Color(0xFF0E3165),
                    fontWeight = FontWeight(700),
                    fontSize = 24.sp,
                    lineHeight = 28.sp
                )
                Text(
                    text = "КиноПоиск",
                    color = Color(0xFF0E3165),
                    fontWeight = FontWeight(500),
                    fontSize = 16.sp,
                    lineHeight = 16.sp
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = film.description,
                fontWeight = FontWeight(400),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                modifier = Modifier
                    .fillMaxWidth(),
                overflow = TextOverflow.Clip,
                softWrap = true
            )
        }
    }
}