import { IsEmail, IsString, IsNotEmpty, MinLength,Length } from 'class-validator';

export class LoginDTO {
  @IsEmail()
  email!: string;

  @MinLength(6)
  password!: string;
}

export class AuthResponseDTO {
  constructor(
    public accessToken: string,
  ) {}
}

export class CrearUsuarioDTO {
  @IsNotEmpty()
  nombres!: string;

  @IsNotEmpty()
  apellidos!: string;

  @IsEmail()
  email!: string;

  @Length(6)
  password!: string;
}

export class UsuarioRespuestaDTO {
  id!: string;
  nombres!: string;
  apellidos!: string;
  email!: string;
  rol!: string;
}