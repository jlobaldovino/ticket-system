import { Body, Controller, Get, Param, ParseUUIDPipe, Post, UseGuards } from '@nestjs/common';
import { UsersService } from './users.service';
import { ApiTags, ApiOperation, ApiCreatedResponse, ApiBearerAuth, ApiOkResponse, ApiParam } from '@nestjs/swagger';
import { UsuarioRespuestaDTO, CrearUsuarioDTO } from 'src/auth/auth.dto';
import { JwtAuthGuard } from 'src/common/guards/jwt.guard';

@ApiTags('Usuarios')
@Controller('usuarios')
export class UsersController {
  constructor(private readonly usersService: UsersService) {}

  @Post()
  @ApiOperation({ summary: 'Crear un nuevo usuario' })
  @ApiCreatedResponse({ type: UsuarioRespuestaDTO })
  async crear(@Body() dto: CrearUsuarioDTO): Promise<UsuarioRespuestaDTO> {
    return this.usersService.crearUsuario(dto);
  }

  @Get(':id')
  @UseGuards(JwtAuthGuard)
  @ApiBearerAuth()
  @ApiOperation({ summary: 'Obtener usuario por ID' })
  @ApiParam({ name: 'id', description: 'UUID del usuario' })
  @ApiOkResponse({ type: UsuarioRespuestaDTO })
  async obtenerPorId(@Param('id', new ParseUUIDPipe()) id: string) {
    return this.usersService.obtenerPorId(id);
  }





  
}
